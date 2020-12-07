package com.example.android.trackmysleepquality.overview

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.asDomainModel
import com.example.android.trackmysleepquality.database.getDatabase
import com.example.android.trackmysleepquality.network.MarsApi
import com.example.android.trackmysleepquality.network.MarsApiFilter
import com.example.android.trackmysleepquality.network.MarsProperty
import com.example.android.trackmysleepquality.repository.VideosRepository
import kotlinx.coroutines.*

enum class MarsApiStatus{ LOADING, ERROR, DONE}
/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel (application: Application) : AndroidViewModel(application) {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    //Just the first response from the API
    private val _properties = MutableLiveData<List<MarsProperty>>()

    //The external immutable LiveData for the request status String
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    //Navigate to the detail fragment
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    //Coroutine Job and Coroutine Scope
    val coroutineJob = Job()
    val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application.applicationContext)
    private val videosRepository = VideosRepository(database)

    init {
        viewModelScope.launch {
            videosRepository.refreshRest()
        }
        _properties.value = videosRepository.rest.value
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        coroutineScope.launch {
            val getPropertiesDeferred = MarsApi.retrofitService.getProperties()
            _status.value = MarsApiStatus.LOADING
            try {
                if (getPropertiesDeferred.size > 0){
                    _status.value = MarsApiStatus.DONE
                    _properties.value = getPropertiesDeferred
                }
            }catch (t: Throwable){
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    fun displayPropertyDetails(marsProperty: MarsProperty){
        _navigateToSelectedProperty.value = marsProperty
    }

    fun displayPropertiesDetailComplete(){
        _navigateToSelectedProperty.value = null
    }

    fun updateFilter(filter: MarsApiFilter){
        getMarsRealEstateProperties(filter)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }
}
