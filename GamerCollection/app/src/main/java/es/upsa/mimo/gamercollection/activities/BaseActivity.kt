package es.upsa.mimo.gamercollection.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import es.upsa.mimo.gamercollection.R


open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }
}