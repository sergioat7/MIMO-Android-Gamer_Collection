package es.upsa.mimo.gamercollection.activities

import android.os.Bundle
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.base.BaseActivity

class RegisterActivity : BaseActivity() {

    // MARK: - Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
}
