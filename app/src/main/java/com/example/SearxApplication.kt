package com.example

import android.app.Application
import com.example.data.downloader.DownloadEngine
import com.example.data.local.AppDatabase
import com.example.data.repository.DownloadRepository

class SearxApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: DownloadRepository
        private set

    lateinit var downloadEngine: DownloadEngine
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        repository = DownloadRepository(database.downloadDao())
        downloadEngine = DownloadEngine(this)
    }
}
