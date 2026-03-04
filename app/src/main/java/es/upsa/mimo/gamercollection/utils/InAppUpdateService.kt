package es.upsa.mimo.gamercollection.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import es.upsa.mimo.gamercollection.domain.UserRepository
import es.upsa.mimo.gamercollection.domain.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class InAppUpdateService @Inject constructor(
    activity: FragmentActivity,
    private val userRepository: UserRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    //region Private properties
    private val appUpdateManager = AppUpdateManagerFactory.create(activity)
    private val listener: InstallStateUpdatedListener
    private var appUpdateType = AppUpdateType.FLEXIBLE
    private val _installStatus = MutableLiveData(InstallStatus.UNKNOWN)
    private val inAppUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>
    //endregion

    //region Public properties
    val installStatus: LiveData<Int> = _installStatus
    //endregion

    //region Lifecycle methods
    init {

        listener = InstallStateUpdatedListener { state ->
            _installStatus.value = state.installStatus
        }
        appUpdateManager.registerListener(listener)

        inAppUpdateLauncher =
            activity.registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult(),
            ) {
                if (it.resultCode == AppCompatActivity.RESULT_OK && isImmediateUpdate()) {
                    _installStatus.value = InstallStatus.INSTALLED
                } else if (it.resultCode != AppCompatActivity.RESULT_OK) {
                    _installStatus.value = InstallStatus.CANCELED
                }
            }
    }
    //endregion

    //region Public methods
    fun checkVersion() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->

            if (isUpdateDownloading(info)) {
                _installStatus.value = InstallStatus.DOWNLOADED
            } else if (isUpdateAvailable(info)) {
                CoroutineScope(ioDispatcher).launch {
                    val isThereMandatoryUpdate = userRepository.isThereMandatoryUpdate()
                    if (isThereMandatoryUpdate) {
                        startUpdate(info, AppUpdateType.IMMEDIATE)
                    } else {
                        startUpdate(info, AppUpdateType.FLEXIBLE)
                    }
                }
            } else {
                _installStatus.value = InstallStatus.INSTALLED
            }
        }
        appUpdateManager.appUpdateInfo.addOnFailureListener {
            _installStatus.value = InstallStatus.INSTALLED
        }
    }

    fun isImmediateUpdate() = appUpdateType == AppUpdateType.IMMEDIATE

    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->

            if (isFlexibleUpdate() && isUpdateAlreadyInstalled(info)) {
                _installStatus.value = InstallStatus.DOWNLOADED + InstallStatus.INSTALLED
            } else if (isImmediateUpdate() && isUpdateDownloading(info)) {
                startUpdate(info, AppUpdateType.IMMEDIATE)
            }
        }
    }

    fun onDestroy() {
        appUpdateManager.unregisterListener(listener)
    }
    //endregion

    //region Private methods
    private fun isFlexibleUpdate() = appUpdateType == AppUpdateType.FLEXIBLE

    private fun isUpdateAlreadyInstalled(info: AppUpdateInfo) =
        info.installStatus == InstallStatus.DOWNLOADED

    private fun isUpdateAvailable(info: AppUpdateInfo) =
        info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

    private fun isUpdateDownloading(info: AppUpdateInfo) =
        info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

    private fun startUpdate(info: AppUpdateInfo, type: Int) {
        appUpdateManager.startUpdateFlowForResult(
            info,
            inAppUpdateLauncher,
            AppUpdateOptions.newBuilder(type).build(),
        )
        appUpdateType = type
    }
    //endregion
}