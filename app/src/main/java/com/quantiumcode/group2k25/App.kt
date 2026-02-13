package com.quantiumcode.group2k25

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.quantiumcode.group2k25.di.AppContainer

class App : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        // Force dark theme to match web portal
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        container = AppContainer(this)
    }
}
