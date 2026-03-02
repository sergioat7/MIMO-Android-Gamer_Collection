package es.upsa.mimo.gamercollection.presentation.login

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.presentation.base.BaseActivity

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        onBackPressedDispatcher.addCallback {
            moveTaskToBack(true)
        }
    }
    //endregion
}