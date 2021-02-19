package com.hyperliteapps.pricechecker

import android.app.Application
import org.koin.android.ext.android.startKoin

//Custom application class, used in this instance to start Koin for DI
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(viewModelModule, repositoryModule))
    }
}