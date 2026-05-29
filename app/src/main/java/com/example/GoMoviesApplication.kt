package com.example

import android.app.Application
import com.example.data.api.TMDBApiService
import com.example.data.repository.MovieRepository
import com.example.database.AppDatabase

class GoMoviesApplication : Application() {
    
    // Dependency Container / Service Locator
    val database: AppDatabase by lazy { 
        AppDatabase.getInstance(this) 
    }
    
    val apiService: TMDBApiService by lazy { 
        TMDBApiService.create() 
    }
    
    val repository: MovieRepository by lazy { 
        MovieRepository(apiService, database.watchlistDao()) 
    }

    override fun onCreate() {
        super.onCreate()
    }
}
