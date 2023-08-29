package com.alvaroquintana.adivinaperro.ui.result

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.DialogSaveRecordBinding
import com.alvaroquintana.adivinaperro.databinding.ResultFragmentBinding
import com.alvaroquintana.adivinaperro.ui.game.GameActivity
import com.alvaroquintana.adivinaperro.ui.ranking.RankingActivity
import com.alvaroquintana.adivinaperro.utils.*
import com.alvaroquintana.adivinaperro.utils.Constants.POINTS
import com.alvaroquintana.domain.App
import com.alvaroquintana.domain.User
import org.koin.androidx.viewmodel.ext.android.viewModel


class ResultFragment : Fragment() {
    private lateinit var binding: ResultFragmentBinding
    private val resultViewModel: ResultViewModel by viewModel()
    private var gamePoints = 0

    companion object {
        fun newInstance() = ResultFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = ResultFragmentBinding.inflate(inflater)
        val root = binding.root


        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
            MediaPlayer.create(context, R.raw.ladrido).start()
        }
        gamePoints = activity?.intent?.extras?.getInt(POINTS)!!

        val textResult: TextView = root.findViewById(R.id.textResult)
        textResult.text = resources.getString(R.string.result, gamePoints)

        resultViewModel.getPersonalRecord(gamePoints, requireContext())
        resultViewModel.setPersonalRecordOnServer(gamePoints)

        val btnContinue: TextView = root.findViewById(R.id.btnContinue)
        btnContinue.setSafeOnClickListener { resultViewModel.navigateToGame() }

        val btnShare: TextView = root.findViewById(R.id.btnShare)
        btnShare.setSafeOnClickListener { resultViewModel.navigateToShare(gamePoints) }

        val btnRate: TextView = root.findViewById(R.id.btnRate)
        btnRate.setSafeOnClickListener { resultViewModel.navigateToRate() }

        val btnRanking: TextView = root.findViewById(R.id.btnRanking)
        btnRanking.setSafeOnClickListener { resultViewModel.navigateToRanking() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
        resultViewModel.progress.observe(viewLifecycleOwner, Observer(::updateProgress))
        resultViewModel.list.observe(viewLifecycleOwner, Observer(::fillAppList))
        resultViewModel.personalRecord.observe(viewLifecycleOwner, Observer(::fillPersonalRecord))
        resultViewModel.worldRecord.observe(viewLifecycleOwner, Observer(::fillWorldRecord))
        resultViewModel.showingAds.observe(viewLifecycleOwner, Observer(::loadAd))
    }

    private fun loadAd(model: ResultViewModel.UiModel) {
        if (model is ResultViewModel.UiModel.ShowAd)
            (activity as ResultActivity).showAd(model.show)
    }

    private fun fillWorldRecord(recordWorldPoints: String) {
        binding.textWorldRecord.text = resources.getString(R.string.world_record, recordWorldPoints)
    }

    private fun fillPersonalRecord(points: String) {
        binding.textPersonalRecord.text = resources.getString(R.string.personal_record, points)
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

    private fun navigate(navigation: ResultViewModel.Navigation) {
        when (navigation) {
            ResultViewModel.Navigation.Game -> {
                activity?.finishAfterTransition()
                activity?.startActivity<GameActivity> {}
            }
            ResultViewModel.Navigation.Rate -> rateApp(requireContext())
            ResultViewModel.Navigation.Ranking -> activity?.startActivity<RankingActivity> {}
            is ResultViewModel.Navigation.Share -> shareApp(requireContext(), navigation.points)
            is ResultViewModel.Navigation.Open -> openAppOnPlayStore(navigation.url)
            is ResultViewModel.Navigation.Dialog -> showEnterNameDialog(navigation.points)
        }
    }

    private fun openAppOnPlayStore(appPackageName: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (notFoundException: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    private fun showEnterNameDialog(points: String) {
        lateinit var dialogBinding: DialogSaveRecordBinding

        Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(dialogBinding.root)
            dialogBinding.btnSubmit.setSafeOnClickListener {
                resultViewModel.saveTopScore(User(dialogBinding.editTextWorldRecord.text.toString(), points.toInt()))
                dismiss()
            }
            show()
        }
    }
}
