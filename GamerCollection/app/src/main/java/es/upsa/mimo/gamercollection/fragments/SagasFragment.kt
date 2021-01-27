package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.activities.SagaDetailActivity
import es.upsa.mimo.gamercollection.adapters.OnItemClickListener
import es.upsa.mimo.gamercollection.adapters.SagasAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.base.BaseModel
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.SagasViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.SagasViewModel
import kotlinx.android.synthetic.main.fragment_sagas.*

class SagasFragment : BaseFragment(), OnItemClickListener {

    //MARK: - Private properties

    private lateinit var viewModel: SagasViewModel
    private lateinit var sagasAdapter: SagasAdapter

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
        swipe_refresh_layout.setColorSchemeResources(R.color.color3)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color2)
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
        })
    }

    private fun showData(sagas: List<SagaResponse>) {

        sagasAdapter.resetList()

        val items = mutableListOf<BaseModel<Int>>()
        val expandedIds = mutableListOf<Int>()
        for (saga in sagas) {

            items.add(saga)
            items.addAll(saga.games.sortedBy { it.releaseDate })
            expandedIds.add(saga.id)
        }
        sagasAdapter.setItems(items)
        sagasAdapter.setExpandedIds(expandedIds)
        sagasAdapter.notifyDataSetChanged()

        layout_empty_list.visibility = if (sagas.isNotEmpty()) View.GONE else View.VISIBLE
        swipe_refresh_layout.visibility = if (sagas.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
