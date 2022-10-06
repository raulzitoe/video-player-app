package com.silverorange.videoplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val service: ApiInterface =
        ServiceBuilder.getInstance().create(ApiInterface::class.java)
    val videoList: MutableLiveData<List<VideoModel>> = MutableLiveData()

    init {
        getVideos()
    }

    fun getVideos(){
        viewModelScope.launch {
            service.getVideos().body()?.let {
                videoList.postValue(it)
            }
        }
    }
}