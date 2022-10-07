package com.silverorange.videoplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress

class MainViewModel : ViewModel() {
    private val service: ApiInterface =
        ServiceBuilder.getInstance().create(ApiInterface::class.java)
    val videoList: MutableLiveData<List<VideoModel>> = MutableLiveData()
    var error = MutableLiveData(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (isInternetAvailable()) {
                getVideos()
            } else {
                error.postValue(true)
            }
        }

    }

    fun getVideos() {
        viewModelScope.launch {
            service.getVideos().body()?.let { response ->
                val sortedList = response.sortedBy { it.publishedAt }
                videoList.postValue(sortedList)
            }
        }
    }

    fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("google.com")
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }
}