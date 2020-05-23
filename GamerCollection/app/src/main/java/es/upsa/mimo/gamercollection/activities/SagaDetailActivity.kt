package es.upsa.mimo.gamercollection.activities

import android.os.Bundle
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.base.BaseActivity
import es.upsa.mimo.gamercollection.fragments.SagaDetailFragment

class SagaDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.SAGA_DETAIL)
        val sagaId = intent.getIntExtra("sagaId", 0)
        setContentView(R.layout.activity_saga_detail)

        val sagaDetailFragment = SagaDetailFragment()
        if (sagaId > 0) {
            val bundle = Bundle()
            bundle.putInt("sagaId", sagaId)
            sagaDetailFragment.arguments = bundle
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.saga_detail_fragment_placeholder, sagaDetailFragment)
        transaction.commit()
    }
}
