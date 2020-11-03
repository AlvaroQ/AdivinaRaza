package com.alvaroquintana.adivinaperro.ui.result

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.ResultFragmentBinding
import com.alvaroquintana.adivinaperro.ui.game.GameActivity
import com.alvaroquintana.adivinaperro.utils.Constants.POINTS
import com.alvaroquintana.adivinaperro.utils.glideLoadingGif
import com.alvaroquintana.adivinaperro.utils.log
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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = ResultFragmentBinding.inflate(inflater)
        val root = binding.root

        MediaPlayer.create(context, R.raw.ladrido).start()
        val points : Int = activity?.intent?.extras?.getInt(POINTS)!!
        val textResult: TextView = root.findViewById(R.id.textResult)
        textResult.text = resources.getString(R.string.result, points)

        val textPersonalRecord: TextView = root.findViewById(R.id.textPersonalRecord)
        val personalRecord = resultViewModel.getPersonalRecord(requireContext())
        if(points > personalRecord) {
            resultViewModel.savePersonalRecord(requireContext(), points)
            textPersonalRecord.text = resources.getString(R.string.personal_record, points)
        }
        else {
            textPersonalRecord.text = resources.getString(R.string.personal_record, personalRecord)
        }
        val textWorldRecord: TextView = root.findViewById(R.id.textWorldRecord)
        textWorldRecord.text = resources.getString(R.string.world_record, points)


        val btnReturn: TextView = root.findViewById(R.id.btnContinue)
        btnReturn.setSafeOnClickListener { resultViewModel.navigateToGame() }

        val btnShare: TextView = root.findViewById(R.id.btnShare)
        btnShare.setSafeOnClickListener { resultViewModel.navigateToShare(points) }

        val btnRate: TextView = root.findViewById(R.id.btnRate)
        btnRate.setSafeOnClickListener { resultViewModel.navigateToRate() }

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
            is ResultViewModel.Navigation.Share -> {
                shareApp(navigation.points)
            }
            ResultViewModel.Navigation.Rate -> {
                rateApp()
            }
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

    private fun shareApp(points: Int) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = resources.getString(R.string.share_message, points)
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)))
        } catch (e: Exception) {
            log(getString(R.string.share), e.toString())
        }
    }

    private fun rateApp() {
        val uri: Uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
        }
    }
}
