package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.core.content.ContextCompat
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.network.apiClient.SagaAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.SagaRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_saga_detail.*

class SagaDetailFragment : BaseFragment() {

    private var sagaId: Int? = null
    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var sagaRepository: SagaRepository
    private lateinit var sagaAPIClient: SagaAPIClient
    private var menu: Menu? = null
    private var currentSaga: SagaResponse? = null
    private var newGames: List<GameResponse> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sagaId = this.arguments?.getInt("sagaId")
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_saga_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        sagaRepository = SagaRepository(requireContext())
        sagaAPIClient = SagaAPIClient(resources, sharedPrefHandler)

        initializeUI()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
        menu.clear()
        inflater.inflate(R.menu.edit_details_toolbar_menu, menu)
        menu.findItem(R.id.action_edit).isVisible = currentSaga != null
        menu.findItem(R.id.action_save).isVisible = currentSaga == null
        menu.findItem(R.id.action_cancel).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_edit -> {
                editSaga()
                return true
            }
            R.id.action_save -> {
                saveSaga()
                return true
            }
            R.id.action_cancel -> {
                cancelEdition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        button_add_game.setOnClickListener { addGame() }
        button_delete_saga.setOnClickListener { deleteSaga() }
    }

    private fun loadData() {

        sagaId?.let {

            showLoading()
            currentSaga = sagaRepository.getSaga(it)
            currentSaga?.let { saga ->
                newGames = saga.games
            }
            hideLoading()
        }

        showData(currentSaga)
        enableEdition(currentSaga == null)
    }

    private fun showData(saga: SagaResponse?) {

        currentSaga = saga
        saga?.let { saga ->

            edit_text_name.setText(saga.name)
            //TODO show games
        }
    }

    private fun enableEdition(enable: Boolean) {

        val inputTypeText = if (enable) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color2)

        edit_text_name.setReadOnly(!enable, inputTypeText, backgroundColor)
        button_add_game.visibility = if(enable) View.VISIBLE else View.GONE
    }

    private fun addGame() {
        //TODO
    }

    private fun deleteSaga() {

        currentSaga?.let {
            showPopupConfirmationDialog(resources.getString(R.string.SAGA_DETAIL_DELETE_CONFIRMATION)) {

//                showLoading()
//                sagaAPIClient.deleteSaga(it.id, {
//                    sagaRepository.deleteSaga(it)
//
//                    hideLoading()
//                    activity?.finish()
//                }, {
//                    manageError(it)
//                })
            }
        }
    }

    private fun editSaga(){

        showEditButton(true)
        enableEdition(true)
    }

    private fun saveSaga() {

        val saga = SagaResponse(
            sagaId ?: 0,
            edit_text_name.text.toString(),
            newGames
        )

        showLoading()
        if (currentSaga != null) {

            sagaAPIClient.setSaga(saga, {
                sagaRepository.updateSaga(it)

                currentSaga = it
                cancelEdition()
                hideLoading()
            }, {
                manageError(it)
            })
        } else {

            sagaAPIClient.createSaga(saga, {
                sagaAPIClient.getSagas({ sagas ->

                    for (saga in sagas) {
                        sagaRepository.insertSaga(saga)
                    }
                    hideLoading()
                    activity?.finish()
                }, {
                    manageError(it)
                })
            }, {
                manageError(it)
            })
        }
    }

    private fun cancelEdition(){

        showEditButton(false)
        showData(currentSaga)
        enableEdition(false)
    }

    private fun showEditButton(hidden: Boolean) {

        menu?.let {
            it.findItem(R.id.action_edit).isVisible = !hidden
            it.findItem(R.id.action_save).isVisible = hidden
            it.findItem(R.id.action_cancel).isVisible = hidden
        }
    }
}
