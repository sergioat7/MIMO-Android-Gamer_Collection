package es.upsa.mimo.gamercollection.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.activities.SagaDetailActivity
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.adapters.SagasAdapter
import es.upsa.mimo.gamercollection.base.BaseModel
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentSagasBinding
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.SagasViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.SagasViewModel

class SagasFragment : BindingFragment<FragmentSagasBinding>(), OnItemClickListener {

    //region Private properties
    private lateinit var viewModel: SagasViewModel
    private lateinit var sagasAdapter: SagasAdapter
    private val scrollPosition = ObservableField<ScrollPosition>()
    //endregion

    //region Lifecycle methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchSagas()
    }

    override fun onPause() {
        super.onPause()
        viewModel.expandedIds = sagasAdapter.getExpandedIds()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.sagas_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_synchronize -> {

                openSyncPopup()
                return true
            }
            R.id.action_add -> {

                launchActivity(SagaDetailActivity::class.java)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    //region Interface methods
    override fun onItemClick(id: Int) {

        val params = mapOf(Constants.SAGA_ID to id)
        launchActivityWithExtras(SagaDetailActivity::class.java, params)
    }

    override fun onSubItemClick(id: Int) {

        val params = mapOf(Constants.GAME_ID to id)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
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

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            SagasViewModelFactory(application)
        ).get(SagasViewModel::class.java)
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
                this@SagasFragment.viewModel.platforms,
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

    private fun setupBindings() {

        viewModel.sagasLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {

                binding.swipeRefreshLayout.isRefreshing = false
                hideLoading()
            }
        })

        viewModel.sagasError.observe(viewLifecycleOwner, { error ->

            hideLoading()
            manageError(error)
        })

        viewModel.sagas.observe(viewLifecycleOwner, {

            showData(it)
            setTitle(it.size)
        })
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

    private fun setTitle(sagasCount: Int) {

        val title =
            resources.getQuantityString(R.plurals.sagas_number_title, sagasCount, sagasCount)
        (activity as AppCompatActivity?)?.supportActionBar?.title = title
    }
    //endregion
}
