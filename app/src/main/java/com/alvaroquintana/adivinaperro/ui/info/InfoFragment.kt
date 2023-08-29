package com.alvaroquintana.adivinaperro.ui.info

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.databinding.InfoFragmentBinding
import com.alvaroquintana.adivinaperro.ui.select.SelectActivity
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_ITEM_EACH_LOAD
import com.alvaroquintana.adivinaperro.utils.glideLoadingGif
import com.alvaroquintana.domain.Dog
import org.koin.androidx.viewmodel.ext.android.viewModel


class InfoFragment : Fragment() {
    private lateinit var binding: InfoFragmentBinding
    private val infoViewModel: InfoViewModel by viewModel()
    private var currentPage = 0
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var adapter: InfoListAdapter

    companion object {
        fun newInstance() = InfoFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = InfoFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoViewModel.navigation.observe(viewLifecycleOwner, Observer(::navigate))
        infoViewModel.dogList.observe(viewLifecycleOwner, Observer(::fillDogList))
        infoViewModel.updateDogList.observe(viewLifecycleOwner, Observer(::updateDogList))
        infoViewModel.progress.observe(viewLifecycleOwner, Observer(::loadAdAndProgress))
        infoViewModel.showingAds.observe(viewLifecycleOwner, Observer(::loadAdAndProgress))
    }

    private fun loadAdAndProgress(model: InfoViewModel.UiModel) {
        when(model) {
            is InfoViewModel.UiModel.ShowAd -> {
                (activity as InfoActivity).showAd(model.show)
            }
            is InfoViewModel.UiModel.ShowReewardAd -> {
                (activity as InfoActivity).showRewardedAd(model.show)
            }
            is InfoViewModel.UiModel.Loading -> {
                if (model.show) {
                    glideLoadingGif(activity as InfoActivity, binding.imagenLoading)
                    binding.imagenLoading.visibility = View.VISIBLE
                } else {
                    binding.imagenLoading.visibility = View.GONE
                }
            }
        }
    }

    private fun fillDogList(dogList: MutableList<Dog>) {
        adapter = InfoListAdapter(requireContext(), dogList)
        binding.recyclerviewInfo.adapter = adapter
        setRecyclerViewScrollListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDogList(dogList: MutableList<Dog>) {
        adapter.update(dogList)
        adapter.notifyDataSetChanged()
        setRecyclerViewScrollListener()
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager?.itemCount
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                if (totalItemCount == lastVisibleItemPosition + 1) {
                    binding.recyclerviewInfo.removeOnScrollListener(scrollListener)

                    if(currentPage * TOTAL_ITEM_EACH_LOAD < TOTAL_BREED) {
                        Log.d("MyTAG", "Load new list")
                        currentPage++
                        infoViewModel.loadMoreDogList(currentPage)
                    }

                    if(currentPage % 4 == 0) infoViewModel.showRewardedAd()
                }
            }
        }
        binding.recyclerviewInfo.addOnScrollListener(scrollListener)
    }

    private fun navigate(navigation: InfoViewModel.Navigation) {
        when (navigation) {
            InfoViewModel.Navigation.Select -> {
                activity?.startActivity<SelectActivity> {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
        }
    }
}
