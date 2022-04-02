package com.alvaroquintana.adivinaperro.ui.game

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.GameFragmentBinding
import com.alvaroquintana.adivinaperro.ui.result.ResultActivity
import com.alvaroquintana.adivinaperro.utils.glideLoadBase64
import com.alvaroquintana.adivinaperro.utils.glideLoadingGif
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import kotlinx.coroutines.*
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel
import java.util.concurrent.TimeUnit
import com.alvaroquintana.adivinaperro.utils.Constants.POINTS
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import com.alvaroquintana.adivinaperro.common.traslationAnimation
import com.alvaroquintana.adivinaperro.common.traslationAnimationFadeIn


class GameFragment : Fragment() {
    private val gameViewModel: GameViewModel by lifecycleScope.viewModel(this)
    private lateinit var binding: GameFragmentBinding

    lateinit var imageLoading: ImageView
    lateinit var imageQuiz: ImageView
    lateinit var btnOptionOne: TextView
    lateinit var btnOptionTwo: TextView
    lateinit var btnOptionThree: TextView
    lateinit var btnOptionFour: TextView

    private var life: Int = 3
    private var stage: Int = 1
    private var points: Int = 0

    companion object {
        fun newInstance() = GameFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = GameFragmentBinding.inflate(inflater)
        val root = binding.root

        imageLoading = root.findViewById(R.id.imagenLoading)
        imageQuiz = root.findViewById(R.id.imageQuiz)
        btnOptionOne = root.findViewById(R.id.btnOptionOne)
        btnOptionTwo = root.findViewById(R.id.btnOptionTwo)
        btnOptionThree = root.findViewById(R.id.btnOptionThree)
        btnOptionFour = root.findViewById(R.id.btnOptionFour)

        btnOptionOne.setSafeOnClickListener {
            btnOptionOne.isSelected = !btnOptionOne.isSelected
            checkResponse()
        }

        btnOptionTwo.setSafeOnClickListener {
            btnOptionTwo.isSelected = !btnOptionTwo.isSelected
            checkResponse()
        }

        btnOptionThree.setSafeOnClickListener {
            btnOptionThree.isSelected = !btnOptionThree.isSelected
            checkResponse()
        }

        btnOptionFour.setSafeOnClickListener {
            btnOptionFour.isSelected = !btnOptionFour.isSelected
            checkResponse()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
        gameViewModel.progress.observe(viewLifecycleOwner, Observer(::loadAdAndProgress))
        gameViewModel.question.observe(viewLifecycleOwner, Observer(::drawQuestionImage))
        gameViewModel.showingAds.observe(viewLifecycleOwner, Observer(::loadAdAndProgress))
        gameViewModel.responseOptions.observe(viewLifecycleOwner, Observer(::drawOptionsResponse))
    }

    private fun loadAdAndProgress(model: GameViewModel.UiModel) {
        when(model) {
            is GameViewModel.UiModel.ShowBannerAd -> {
                (activity as GameActivity).showBannerAd()
            }
            is GameViewModel.UiModel.ShowReewardAd -> {
                (activity as GameActivity).showRewardedAd(model.show)
            }
            is GameViewModel.UiModel.Loading -> updateProgress(model.show)
        }
    }

    private fun navigate(navigation: GameViewModel.Navigation?) {
        when (navigation) {
            is GameViewModel.Navigation.Result -> {
                activity?.startActivity<ResultActivity> { putExtra(POINTS, points) }
            }
        }
    }

    private fun updateProgress(isShowing: Boolean) {
        if (isShowing) {
            glideLoadingGif(activity as GameActivity, imageLoading)
            imageLoading.visibility = View.VISIBLE

            btnOptionOne.isSelected = false
            btnOptionTwo.isSelected = false
            btnOptionThree.isSelected = false
            btnOptionFour.isSelected = false

            enableBtn(false)
        } else {
            imageLoading.visibility = View.GONE

            btnOptionOne.background = context?.getDrawable(R.drawable.button)
            btnOptionTwo.background = context?.getDrawable(R.drawable.button)
            btnOptionThree.background = context?.getDrawable(R.drawable.button)
            btnOptionFour.background = context?.getDrawable(R.drawable.button)

            enableBtn(true)
            (activity as GameActivity).writeStage(stage)
        }
    }

    private fun drawQuestionImage(imageBase64: String) {
        glideLoadBase64(activity as GameActivity, imageBase64, imageQuiz)
    }

    private fun drawOptionsResponse(optionsListByPos: MutableList<String>) {
        var delay = 150L
        if(stage == 1) {
            delay = 0L
            binding.containerButtons.traslationAnimationFadeIn()
        }
        else binding.containerButtons.traslationAnimation()

        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.MILLISECONDS.toMillis(delay))
            withContext(Dispatchers.Main) {
                btnOptionOne.text = optionsListByPos[0]
                btnOptionTwo.text = optionsListByPos[1]
                btnOptionThree.text = optionsListByPos[2]
                btnOptionFour.text = optionsListByPos[3]
            }
        }
    }

    private fun checkResponse() {
        enableBtn(false)
        stage += 1

        drawCorrectResponse(gameViewModel.getNameBreedCorrect()!!)
        nextScreen()
    }

    private fun deleteLife() {
        life--
        (activity as GameActivity).writeDeleteLife(life)
    }

    private fun drawCorrectResponse(dogNameCorrect: String) {
        when {
            btnOptionOne.text == dogNameCorrect -> {
                btnOptionOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseCorrect))
                when {
                    btnOptionOne.isSelected -> {
                        soundSuccess()
                        points += 1
                    }
                    btnOptionTwo.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionThree.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionFour.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    else -> {
                        soundFail()
                        deleteLife()
                    }
                }
            }
            btnOptionTwo.text == dogNameCorrect -> {
                btnOptionTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseCorrect))
                when {
                    btnOptionOne.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionTwo.isSelected -> {
                        soundSuccess()
                        points += 1
                    }
                    btnOptionThree.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionFour.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    else -> {
                        soundFail()
                        deleteLife()
                    }
                }
            }
            btnOptionThree.text == dogNameCorrect -> {
                btnOptionThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseCorrect))
                when {
                    btnOptionOne.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionTwo.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionThree.isSelected -> {
                        soundSuccess()
                        points += 1
                    }
                    btnOptionFour.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    else -> {
                        soundFail()
                        deleteLife()
                    }
                }
            }
            btnOptionFour.text == dogNameCorrect -> {
                btnOptionFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseCorrect))
                when {
                    btnOptionOne.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionTwo.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionThree.isSelected -> {
                        soundFail()
                        deleteLife()
                        btnOptionThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionFour.isSelected -> {
                        soundSuccess()
                        points += 1
                    }
                    else -> {
                        soundFail()
                        deleteLife()
                    }
                }
            }
        }
    }

    private fun enableBtn(isEnable: Boolean) {
        btnOptionOne.isClickable = isEnable
        btnOptionTwo.isClickable = isEnable
        btnOptionThree.isClickable = isEnable
        btnOptionFour.isClickable = isEnable
    }

    private fun nextScreen() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.MILLISECONDS.toMillis(1000))
            withContext(Dispatchers.Main) {
                if(stage > TOTAL_BREED || life < 1) {
                    gameViewModel.navigateToResult(points.toString())
                }
                else {
                    gameViewModel.generateNewStage()
                    if(stage % 10 == 0) gameViewModel.showRewardedAd()
                }
            }
        }
    }

    private fun soundFail() {
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
            MediaPlayer.create(context, R.raw.fail).start()
        }
    }

    private fun soundSuccess() {
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
            MediaPlayer.create(context, R.raw.success).start()
        }
    }
}
