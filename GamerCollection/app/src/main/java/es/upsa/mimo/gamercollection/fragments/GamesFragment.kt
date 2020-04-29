package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import es.upsa.mimo.gamercollection.R

/**
 * A simple [Fragment] subclass.
 */
class GamesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

}
