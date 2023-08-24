package es.upsa.mimo.gamercollection.ui.sagas

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.interfaces.OnItemClickListener
import es.upsa.mimo.gamercollection.models.base.BaseModel
import es.upsa.mimo.gamercollection.ui.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentSagasBinding
import es.upsa.mimo.gamercollection.extensions.hideSoftKeyboard
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.ScrollPosition
import es.upsa.mimo.gamercollection.utils.StatusBarStyle

@AndroidEntryPoint
class SagasFragment : BindingFragment<FragmentSagasBinding>(), OnItemClickListener {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    override val hasOptionsMenu = true
    //endregion

    //region Private properties
    private val viewModel: SagasViewModel by viewModels()
    private lateinit var sagasAdapter: SagasAdapter
    private val scrollPosition = ObservableField<ScrollPosition>()
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
        initializeUi()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchSagas()
    }

    override fun onPause() {
        super.onPause()
        viewModel.expandedIds = sagasAdapter.getExpandedIds()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.sagas_toolbar_menu, menu)
        setupSearchView(menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
//            R.id.action_synchronize -> {
//
//                openSyncPopup()
//                return true
//            }
            R.id.action_add -> {

                val action = SagasFragmentDirections.actionSagasFragmentToSagaDetailFragment(-1)
                findNavController().navigate(action)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    //region Interface methods
    override fun onItemClick(id: Int) {

        val action = SagasFragmentDirections.actionSagasFragmentToSagaDetailFragment(id)
        findNavController().navigate(action)
    }

    override fun onSubItemClick(id: Int) {

        val action = SagasFragmentDirections.actionSagasFragmentToGameDetailFragment(id)
        findNavController().navigate(action)
    }

    override fun onLoadMoreItemsClick() {
    }
    //endregion

    //region Public methods
    fun goToStartEndList(view: View) {

        with(binding) {
            when (view) {
                floatingActionButtonStartList -> {

                    recyclerViewSagas.scrollToPosition(0)
                    scrollPosition.set(ScrollPosition.TOP)
                }

                floatingActionButtonEndList -> {

                    val position: Int = sagasAdapter.itemCount - 1
                    recyclerViewSagas.scrollToPosition(position)
                    scrollPosition.set(ScrollPosition.END)
                }
            }
        }
    }
    //endregion

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        setupBindings()

        with(binding) {

            swipeRefreshLayout.apply {
                isEnabled = this@SagasFragment.viewModel.swipeRefresh
                setColorSchemeResources(R.color.colorFinished)
                setProgressBackgroundColorSchemeResource(R.color.colorSecondary)
                setOnRefreshListener {
                    this@SagasFragment.viewModel.loadSagas()
                }
            }

            sagasAdapter = SagasAdapter(
                this@SagasFragment.viewModel.sagas.value?.toMutableList() ?: mutableListOf(),
                mutableListOf(),
                this@SagasFragment
            )
            recyclerViewSagas.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = sagasAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        scrollPosition.set(
                            if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                ScrollPosition.TOP
                            } else if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                ScrollPosition.END
                            } else {
                                ScrollPosition.MIDDLE
                            }
                        )
                    }
                })
            }

            fragment = this@SagasFragment
            viewModel = this@SagasFragment.viewModel
            lifecycleOwner = this@SagasFragment
            position = scrollPosition
        }

        scrollPosition.set(ScrollPosition.TOP)
    }
    //endregion

    //region Private methods
    private fun setupBindings() {

        viewModel.sagasLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {

                binding.swipeRefreshLayout.isRefreshing = false
                hideLoading()
            }
        }

        viewModel.sagasError.observe(viewLifecycleOwner) { error ->

            hideLoading()
            manageError(error)
        }

        viewModel.sagas.observe(viewLifecycleOwner) {
            showData(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showData(sagas: List<SagaResponse>) {

        sagasAdapter.resetList()

        val items = mutableListOf<BaseModel<Int>>()
        for (saga in sagas) {

            items.add(saga)
            if (viewModel.expandedIds.contains(saga.id)) {
                items.addAll(saga.games.sortedBy { it.releaseDate })
            }
        }
        sagasAdapter.setItems(items)
        sagasAdapter.setExpandedIds(viewModel.expandedIds)
        sagasAdapter.notifyDataSetChanged()
    }

    private fun setupSearchView(menu: Menu) {

        val menuItem = menu.findItem(R.id.action_search_sagas)
        searchView = menuItem.actionView as SearchView
        searchView?.let { searchView ->

            searchView.queryHint = resources.getString(R.string.search_sagas)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {

                    viewModel.searchSagas(newText)
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {

                    menuItem.collapseActionView()
                    requireActivity().hideSoftKeyboard()
                    return true
                }
            })
        }
        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {

                menu.let {
//                    it.findItem(R.id.action_synchronize).isVisible = false
                    it.findItem(R.id.action_add).isVisible = false
                }
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {

                menu.let {
//                    it.findItem(R.id.action_synchronize).isVisible = true
                    it.findItem(R.id.action_add).isVisible = true
                }
                return true
            }

        })
        setupSearchView(Constants.EMPTY_VALUE)
    }
    //endregion
}
