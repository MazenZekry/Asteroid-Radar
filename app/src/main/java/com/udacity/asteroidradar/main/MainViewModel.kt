package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class FilterAsteroid{
    TODAY,
    WEEK,
    ALL
}
class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val database = AsteroidDatabase.getDatabase(app)
    private val repository = AsteroidRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToDetailFragment = MutableLiveData<Asteroid?>()
    val navigateToDetailFragment
        get() = _navigateToDetailFragment

    private var _filterAsteroid = MutableLiveData(FilterAsteroid.ALL)

    @RequiresApi(Build.VERSION_CODES.O)
    val asteroidList = Transformations.switchMap(_filterAsteroid) {
        when (it!!) {
            FilterAsteroid.WEEK -> repository.weekAsteroids
            FilterAsteroid.TODAY -> repository.todayAsteroids
            else -> repository.allAsteroids
        }
    }

    init {
        viewModelScope.launch {
            refreshAsteroids()
            getPictureOfDay()
        }
    }
    fun onChangeFilter(filter: FilterAsteroid) {
        _filterAsteroid.postValue(filter)
    }

    fun onAsteroidItemClick(data: Asteroid) {
        _navigateToDetailFragment.value = data
    }

    fun onDetailFragmentNavigated() {
        _navigateToDetailFragment.value = null
    }

    private fun refreshAsteroids() {
        viewModelScope.launch {
            try {
                repository.refreshAsteroids()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = repository.getPictureOfDay()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}