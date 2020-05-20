package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.adapters.SagasAdapter
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.network.apiClient.SagaAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.persistence.repositories.SagaRepository
import es.upsa.mimo.gamercollection.persistence.repositories.StateRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_sagas.*

class SagasFragment : BaseFragment(), SagasAdapter.OnItemClickListener {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var sagaAPIClient: SagaAPIClient
    private lateinit var sagaRepository: SagaRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_sagas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        sagaAPIClient = SagaAPIClient(resources, sharedPrefHandler)
        sagaRepository = SagaRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        stateRepository = StateRepository(requireContext())

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
        //TODO
    }

    override fun onGameItemClick(gameId: Int) {

        val params = mapOf("gameId" to gameId)
        launchActivityWithExtras(GameDetailActivity::class.java, params)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        swipe_refresh_layout.setColorSchemeResources(R.color.color3)
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color2)
        swipe_refresh_layout.setOnRefreshListener {
            loadSagas()
        }

        recycler_view_sagas.layoutManager = LinearLayoutManager(requireContext())
        val platforms = platformRepository.getPlatforms()
        val states = stateRepository.getStates()
        recycler_view_sagas.adapter = SagasAdapter(requireContext(), ArrayList(), platforms, states, this)
    }

    private fun getContent() {

        val sagas = sagaRepository.getSagas()

        val adapter = recycler_view_sagas.adapter
        if (adapter is SagasAdapter) {
            adapter.sagas = sagas
            adapter.notifyDataSetChanged()
        }
        layout_empty_list.visibility = if (sagas.isNotEmpty()) View.GONE else View.VISIBLE
        swipe_refresh_layout.visibility = if (sagas.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun add(){
        //TODO
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
