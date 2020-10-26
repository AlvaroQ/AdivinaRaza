package com.alvaroquintana.adivinaperro.ui.result

import android.animation.ObjectAnimator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.ResultFragmentBinding
import com.alvaroquintana.adivinaperro.ui.game.GameActivity
import com.alvaroquintana.adivinaperro.utils.Constants.POINTS
import com.alvaroquintana.adivinaperro.utils.glideLoadingGif
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import com.alvaroquintana.domain.App
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel


class ResultFragment : Fragment() {
    private lateinit var binding: ResultFragmentBinding
    private val resultViewModel: ResultViewModel by lifecycleScope.viewModel(this)

    companion object {
        fun newInstance() = ResultFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ResultFragmentBinding.inflate(inflater)
        val root = binding.root

        MediaPlayer.create(context, R.raw.ladrido).start()
        val points : Float = activity?.intent?.extras?.getFloat(POINTS)!!

        val ratingBar: RatingBar = root.findViewById(R.id.ratingBar)
        ObjectAnimator.ofFloat(ratingBar, "rating", 0f, points).setDuration(500).start()

        val textDiploma: TextView = root.findViewById(R.id.textDiploma)
        val imageResult: ImageView = root.findViewById(R.id.imageResult)
        if(points < 3) {
            textDiploma.text = getString(R.string.nota_result_text_fail)
            imageResult.background = context?.getDrawable(R.drawable.diploma_fail)
        } else {
            textDiploma.text = getString(R.string.nota_result_text_success)
            imageResult.background = context?.getDrawable(R.drawable.diploma_passed)
        }
        imageResult.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_xy_collapse))

        val btnReturn: TextView = root.findViewById(R.id.btnReturn)
        btnReturn.setSafeOnClickListener { resultViewModel.navigateToGame() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
        resultViewModel.progress.observe(viewLifecycleOwner, Observer(::updateProgress))
        resultViewModel.list.observe(viewLifecycleOwner, Observer(::fillAppList))
    }

    private fun fillAppList(appList: MutableList<App>) {
        binding.recyclerviewOtherApps.adapter = AppListAdapter(
            activity as ResultActivity,
            appList,
            resultViewModel::onAppClicked
        )
    }

    private fun updateProgress(model: ResultViewModel.UiModel?) {
        if (model is ResultViewModel.UiModel.Loading && model.show) {
            glideLoadingGif(activity as ResultActivity, binding.imagenLoading)
            binding.imagenLoading.visibility = View.VISIBLE
        } else {
            binding.imagenLoading.visibility = View.GONE
        }
    }

    private fun navigate(navigation: ResultViewModel.Navigation?) {
        when (navigation) {
            ResultViewModel.Navigation.Game -> {
                activity?.startActivity<GameActivity> {}
            }
            is ResultViewModel.Navigation.Open -> {
                openAppOnPlayStore(navigation.url)
            }
        }
    }

    private fun openAppOnPlayStore(appPackageName: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (notFoundException: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }
}
