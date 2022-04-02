package com.alvaroquintana.adivinaperro.ui.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.SelectFragmentBinding
import androidx.lifecycle.Observer
import com.alvaroquintana.adivinaperro.ui.game.GameActivity
import com.alvaroquintana.adivinaperro.ui.info.InfoActivity
import com.alvaroquintana.adivinaperro.ui.settings.SettingsActivity
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel

class SelectFragment : Fragment() {
    private lateinit var binding: SelectFragmentBinding
    private val selectViewModel: SelectViewModel by lifecycleScope.viewModel(this)

    companion object {
        fun newInstance() = SelectFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SelectFragmentBinding.inflate(inflater)
        val root = binding.root

        val btnStart: Button = root.findViewById(R.id.btnStart)
        btnStart.setSafeOnClickListener { selectViewModel.navigateToGame() }

        val btnLearn: Button = root.findViewById(R.id.btnLearn)
        btnLearn.setSafeOnClickListener { selectViewModel.navigateToLearn() }

        val btnSettings: Button = root.findViewById(R.id.btnSettings)
        btnSettings.setSafeOnClickListener { selectViewModel.navigateToSettings() }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
    }

    private fun navigate(navigation: SelectViewModel.Navigation?) {
        when (navigation) {
            SelectViewModel.Navigation.Game -> activity?.startActivity<GameActivity> {}
            SelectViewModel.Navigation.Learn -> activity?.startActivity<InfoActivity> {}
            SelectViewModel.Navigation.Setting ->activity?.startActivity<SettingsActivity> {}
        }
    }
}
