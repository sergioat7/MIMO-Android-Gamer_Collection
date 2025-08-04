package es.upsa.mimo.gamercollection.ui.register

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.ui.base.BaseActivity

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
    }
    //endregion
}
