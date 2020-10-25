package com.alvaroquintana.adivinaperro.ui.game

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import com.alvaroquintana.domain.Dog


class GameFragment : Fragment() {
    private val gameViewModel: GameViewModel by lifecycleScope.viewModel(this)
    private lateinit var binding: GameFragmentBinding

    lateinit var progressGame: ProgressBar
    lateinit var imageLoading: ImageView
    lateinit var imageQuiz: ImageView
    lateinit var btnOptionOne: TextView
    lateinit var btnOptionTwo: TextView
    lateinit var btnOptionThree: TextView
    lateinit var btnOptionFour: TextView
    lateinit var textCounter: TextView

    private var stage: Int = 1
    private var points: Int = 0
    var cTimer: CountDownTimer? = null


    companion object {
        fun newInstance() = GameFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = GameFragmentBinding.inflate(inflater)
        val root = binding.root

        progressGame = root.findViewById(R.id.progressGame)
        imageLoading = root.findViewById(R.id.imagenLoading)
        imageQuiz = root.findViewById(R.id.imageQuiz)
        btnOptionOne = root.findViewById(R.id.btnOptionOne)
        btnOptionTwo = root.findViewById(R.id.btnOptionTwo)
        btnOptionThree = root.findViewById(R.id.btnOptionThree)
        btnOptionFour = root.findViewById(R.id.btnOptionFour)
        textCounter = root.findViewById(R.id.textCounter)

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
        gameViewModel.progress.observe(viewLifecycleOwner, Observer(::updateProgress))
        gameViewModel.question.observe(viewLifecycleOwner, Observer(::drawQuestionImage))
        gameViewModel.responseOptions.observe(viewLifecycleOwner, Observer(::drawOptionsResponse))
    }

    private fun navigate(navigation: GameViewModel.Navigation?) {
        when (navigation) {
            GameViewModel.Navigation.Result -> {
                activity?.startActivity<ResultActivity> { putExtra(POINTS, points.toFloat()) }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        cancelCountDown()
    }

    private fun updateProgress(model: GameViewModel.UiModel?) {
        if (model is GameViewModel.UiModel.Loading && model.show) {
            glideLoadingGif(activity as GameActivity, imageLoading)
            imageQuiz.visibility = View.GONE
            imageLoading.visibility = View.VISIBLE

            btnOptionOne.text = ""
            btnOptionTwo.text = ""
            btnOptionThree.text = ""
            btnOptionFour.text = ""

            btnOptionOne.background = context?.getDrawable(R.drawable.button)
            btnOptionTwo.background = context?.getDrawable(R.drawable.button)
            btnOptionThree.background = context?.getDrawable(R.drawable.button)
            btnOptionFour.background = context?.getDrawable(R.drawable.button)

            btnOptionOne.isSelected = false
            btnOptionTwo.isSelected = false
            btnOptionThree.isSelected = false
            btnOptionFour.isSelected = false
        } else {
            imageLoading.visibility = View.GONE
            imageQuiz.visibility = View.VISIBLE

            btnOptionOne.isClickable = true
            btnOptionTwo.isClickable = true
            btnOptionThree.isClickable = true
            btnOptionFour.isClickable = true
        }
    }

    private fun drawQuestionImage(imageBase64: String) {
        glideLoadBase64(activity as GameActivity, imageBase64, imageQuiz)
    }

    private fun drawOptionsResponse(optionsListByPos: MutableList<String>) {
        btnOptionOne.text = optionsListByPos[0]
        btnOptionTwo.text = optionsListByPos[1]
        btnOptionThree.text = optionsListByPos[2]
        btnOptionFour.text = optionsListByPos[3]

        progressGame.progress = stage
        startCountDown()
    }

    private fun startCountDown() {
        val timeSinceVerificationSent = 10000L
        if(cTimer == null) {
            cTimer = object : CountDownTimer(timeSinceVerificationSent, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    textCounter.text = (millisUntilFinished / 1000).toString()
                }

                override fun onFinish() {
                    MediaPlayer.create(context, R.raw.fail).start()
                    checkResponse()
                }
            }
            (cTimer as CountDownTimer).start()
        }
    }

    private fun cancelCountDown() {
        cTimer = with(cTimer) {
            this?.cancel()
            null
        }
    }

    private fun checkResponse() {
        btnOptionOne.isClickable = false
        btnOptionTwo.isClickable = false
        btnOptionThree.isClickable = false
        btnOptionFour.isClickable = false

        cancelCountDown()
        stage += 1

        drawCorrectResponse(gameViewModel.getNameBreedCorrect()!!)
        nextScreen()
    }

    private fun drawCorrectResponse(dogNameCorrect: String) {
        when {
            btnOptionOne.text == dogNameCorrect -> {
                btnOptionOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseCorrect))
                when {
                    btnOptionOne.isSelected -> {
                        MediaPlayer.create(context, R.raw.success).start()
                        points += 1
                    }
                    btnOptionTwo.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionThree.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionFour.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                }
            }
            btnOptionTwo.text == dogNameCorrect -> {
                btnOptionTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseCorrect))
                when {
                    btnOptionOne.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionTwo.isSelected -> {
                        MediaPlayer.create(context, R.raw.success).start()
                        points += 1
                    }
                    btnOptionThree.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionFour.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                }
            }
            btnOptionThree.text == dogNameCorrect -> {
                btnOptionThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseCorrect))
                when {
                    btnOptionOne.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionTwo.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionThree.isSelected -> {
                        MediaPlayer.create(context, R.raw.success).start()
                        points += 1
                    }
                    btnOptionFour.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                }
            }
            btnOptionFour.text == dogNameCorrect -> {
                btnOptionFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseCorrect))
                when {
                    btnOptionOne.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionTwo.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionThree.isSelected -> {
                        MediaPlayer.create(context, R.raw.fail).start()
                        btnOptionThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.responseFail))
                    }
                    btnOptionFour.isSelected -> {
                        MediaPlayer.create(context, R.raw.success).start()
                        points += 1
                    }
                }
            }
        }
    }

    private fun nextScreen() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(2))
            withContext(Dispatchers.Main) {
                if(stage < 6) gameViewModel.generateNewStage()
                else gameViewModel.navigateToResult()
            }
        }
    }
}
