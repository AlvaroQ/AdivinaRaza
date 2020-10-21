package com.alvaroquintana.adivinaperro.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.GameFragmentBinding
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel
import androidx.lifecycle.Observer


class GameFragment : Fragment() {
    private val viewModel: GameViewModel by lifecycleScope.viewModel(this)

    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = GameFragmentBinding.inflate(inflater)
        val root = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
    }

    private fun navigate(navigation: GameViewModel.Navigation?) {
        when (navigation) {
            GameViewModel.Navigation.Game -> { activity?.startActivity<GameActivity> {} }
        }
    }
}
