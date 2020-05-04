package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment

class GameDetailFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_detail, container, false)
    }

}
