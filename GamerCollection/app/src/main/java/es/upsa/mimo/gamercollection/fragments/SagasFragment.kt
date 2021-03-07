package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.activities.SagaDetailActivity
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.adapters.SagasAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.models.base.BaseModel
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.SagasViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.SagasViewModel
import kotlinx.android.synthetic.main.fragment_sagas.*

class SagasFragment : BaseFragment(), OnItemClickListener {

    //MARK: - Private properties

    private lateinit var viewModel: SagasViewModel
    private lateinit var sagasAdapter: SagasAdapter
    private val scrollPosition = MutableLiveData<ScrollPosition>()

    // MARK: - Lifecycle methods

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_sagas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSagas()
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

        when(item.itemId) {
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

    //MARK: - Interface methods

    override fun onItemClick(id: Int) {

        val params = mapOf(Constants.SAGA_ID to id)
        launchActivityWithExtras(SagaDetailActivity::class.java, params)
    }

    override fun onSubItemClick(id: Int) {

        val params = mapOf(Constants.GAME_ID to id)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, SagasViewModelFactory(application)).get(SagasViewModel::class.java)
        setupBindings()

        swipe_refresh_layout.isEnabled = viewModel.swipeRefresh
        swipe_refresh_layout.setColorSchemeResources(R.color.colorFinished)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.colorSecondary)
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.loadSagas()
        }

        recycler_view_sagas.layoutManager = LinearLayoutManager(requireContext())
        sagasAdapter = SagasAdapter(
            viewModel.sagas.value?.toMutableList() ?: mutableListOf(),
            mutableListOf(),
            viewModel.platforms,
            viewModel.states,
            requireContext(),
            this
        )
        recycler_view_sagas.adapter = sagasAdapter
        recycler_view_sagas.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                scrollPosition.value =
                    if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        ScrollPosition.TOP
                    } else if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        ScrollPosition.END
                    } else {
                        ScrollPosition.MIDDLE
                    }
            }
        })

        scrollPosition.value = ScrollPosition.TOP

        floating_action_button_start_list.setOnClickListener {

            recycler_view_sagas.scrollToPosition(0)
            scrollPosition.value = ScrollPosition.TOP
        }

        floating_action_button_end_list.setOnClickListener {

            val position: Int = sagasAdapter.itemCount - 1
            recycler_view_sagas.scrollToPosition(position)
            scrollPosition.value = ScrollPosition.END
        }
    }

    private fun setupBindings() {

        viewModel.sagasLoading.observe(viewLifecycleOwner, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {

                swipe_refresh_layout.isRefreshing = false
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

        scrollPosition.observe(viewLifecycleOwner, {

            floating_action_button_start_list.visibility = if (it == ScrollPosition.TOP) View.GONE else View.VISIBLE
            floating_action_button_end_list.visibility = if (it == ScrollPosition.END) View.GONE else View.VISIBLE
        })
    }

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

        layout_empty_list.visibility = if (sagas.isNotEmpty()) View.GONE else View.VISIBLE
        swipe_refresh_layout.visibility = if (sagas.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun setTitle(sagasCount: Int) {

        val title = resources.getQuantityString(R.plurals.sagas_number_title, sagasCount, sagasCount)
        (activity as AppCompatActivity?)?.supportActionBar?.title = title
    }
}
