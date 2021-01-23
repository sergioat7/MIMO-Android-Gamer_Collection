package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.BuildConfig
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.activities.SagaDetailActivity
import es.upsa.mimo.gamercollection.adapters.SagasAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.base.BaseModel
import es.upsa.mimo.gamercollection.network.apiClient.SagaAPIClient
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_sagas.*
import javax.inject.Inject

class SagasFragment : BaseFragment(), SagasAdapter.OnItemClickListener {

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler
    @Inject
    lateinit var sagaAPIClient: SagaAPIClient
    @Inject
    lateinit var sagaRepository: SagaRepository
    @Inject
    lateinit var platformRepository: PlatformRepository
    @Inject
    lateinit var stateRepository: StateRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_sagas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = activity?.application
        (application as GamerCollectionApplication).appComponent.inject(this)

        initializeUI()
    }

    override fun onResume() {
        super.onResume()
        getContent()
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
                add()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(sagaId: Int) {

        val params = mapOf("sagaId" to sagaId)
        launchActivityWithExtras(SagaDetailActivity::class.java, params)
    }

    override fun onGameItemClick(gameId: Int) {

        val params = mapOf("gameId" to gameId)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        swipe_refresh_layout.isEnabled = sharedPrefHandler.getSwipeRefresh()
        swipe_refresh_layout.setColorSchemeResources(R.color.color3)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color2)
        swipe_refresh_layout.setOnRefreshListener {
            loadSagas()
        }

        recycler_view_sagas.layoutManager = LinearLayoutManager(requireContext())
        val platforms = platformRepository.getPlatforms()
        val states = stateRepository.getStates()
        recycler_view_sagas.adapter = SagasAdapter(requireContext(), ArrayList(), ArrayList(), platforms, states, this)
    }

    private fun getContent() {

        val sagas = sagaRepository.getSagas().sortedBy { it.name }

        val adapter = recycler_view_sagas.adapter
        if (adapter != null && adapter is SagasAdapter) {
            val items = ArrayList<BaseModel<Int>>()
            val expandedIds = ArrayList<Int>()
            for (saga in sagas) {
                items.add(saga)
                items.addAll(saga.games.sortedBy { it.releaseDate })
                expandedIds.add(saga.id)
            }
            adapter.items = items
            adapter.expandedIds = expandedIds
            adapter.notifyDataSetChanged()
        }
        layout_empty_list.visibility = if (sagas.isNotEmpty()) View.GONE else View.VISIBLE
        swipe_refresh_layout.visibility = if (sagas.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun add(){
        launchActivity(SagaDetailActivity::class.java)
    }

    private fun loadSagas() {

        sagaAPIClient.getSagas({


            for (saga in it) {
                sagaRepository.insertSaga(saga)
            }
            sagaRepository.removeDisableContent(it)
            getContent()
            swipe_refresh_layout.isRefreshing = false
        },{

            manageError(it)
            swipe_refresh_layout.isRefreshing = false
        })
    }
}
