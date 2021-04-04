package es.upsa.mimo.gamercollection.activities

import android.os.Bundle
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.base.BaseActivity

class LoginActivity : BaseActivity() {

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
    //endregion
}