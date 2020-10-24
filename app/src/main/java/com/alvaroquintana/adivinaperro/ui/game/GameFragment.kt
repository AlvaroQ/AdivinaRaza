package com.alvaroquintana.adivinaperro.ui.game

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.GameFragmentBinding
import com.alvaroquintana.adivinaperro.utils.glideLoadBase64
import com.alvaroquintana.adivinaperro.utils.glideLoadingGif
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel


class GameFragment : Fragment() {
    private val gameViewModel: GameViewModel by lifecycleScope.viewModel(this)

    lateinit var imagenLoading: ImageView
    lateinit var imageQuiz: ImageView
    lateinit var btnOptionOne: TextView
    lateinit var btnOptionTwo: TextView
    lateinit var btnOptionThree: TextView
    lateinit var btnOptionFour: TextView
    lateinit var textCounter: TextView

    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameFragmentBinding.inflate(inflater)
        val root = binding.root

        imagenLoading = root.findViewById(R.id.imagenLoading)
        imageQuiz = root.findViewById(R.id.imageQuiz)
        btnOptionOne = root.findViewById(R.id.btnOptionOne)
        btnOptionTwo = root.findViewById(R.id.btnOptionTwo)
        btnOptionThree = root.findViewById(R.id.btnOptionThree)
        btnOptionFour = root.findViewById(R.id.btnOptionFour)
        textCounter = root.findViewById(R.id.textCounter)

        btnOptionOne.setSafeOnClickListener {
            btnOptionOne.isSelected = !btnOptionOne.isSelected
        }

        btnOptionTwo.setSafeOnClickListener {
            btnOptionTwo.isSelected = !btnOptionTwo.isSelected
        }

        btnOptionThree.setSafeOnClickListener {
            btnOptionThree.isSelected = !btnOptionThree.isSelected
        }

        btnOptionFour.setSafeOnClickListener {
            btnOptionFour.isSelected = !btnOptionFour.isSelected
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
            GameViewModel.Navigation.Game -> {
                activity?.startActivity<GameActivity> {}
            }
        }
    }

    private fun updateProgress(model: GameViewModel.UiModel?) {
        if (model is GameViewModel.UiModel.Loading && model.show) {
            glideLoadingGif(activity as GameActivity, imagenLoading)
            imagenLoading.visibility = View.VISIBLE
        } else {
            imagenLoading.visibility = View.GONE
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

        startGame()
    }

    private fun startGame() {
        startCountDown()
    }

    private fun startCountDown() {
        var cTimer: CountDownTimer? = null
        val timeSinceVerificationSent = 10000L
        if(cTimer == null) {
            cTimer = object : CountDownTimer(timeSinceVerificationSent, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    textCounter.text = (millisUntilFinished / 1000).toString()
                }

                override fun onFinish() {

                }
            }
            (cTimer as CountDownTimer).start()
        }
    }
}
