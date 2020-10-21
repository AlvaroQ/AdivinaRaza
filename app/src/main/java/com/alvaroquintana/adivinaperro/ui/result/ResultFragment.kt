package com.alvaroquintana.adivinaperro.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.ResultFragmentBinding
import com.alvaroquintana.adivinaperro.ui.game.GameActivity
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel
import androidx.lifecycle.Observer

class ResultFragment : Fragment() {
    private lateinit var binding: ResultFragmentBinding
    private val resultViewModel: ResultViewModel by lifecycleScope.viewModel(this)

    companion object {
        fun newInstance() = ResultFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ResultFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
    }

    private fun navigate(navigation: ResultViewModel.Navigation?) {
        when (navigation) {
            ResultViewModel.Navigation.Game -> { activity?.startActivity<GameActivity> {} }
        }
    }
}
