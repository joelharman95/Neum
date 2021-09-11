/*
 * *
 *  * Created by Nethaji on 11/9/21 9:12 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 11/9/21 9:12 PM
 *
 */

package com.nis.neum

import android.app.Application
import android.net.ConnectivityManager
import com.facebook.stetho.Stetho
import com.nis.neum.di.NETWORKING_MODULE
import com.nis.neum.di.REPOSITORY_MODULE
import com.nis.neum.di.VIEW_MODEL_MODULE
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class Neum : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        Stetho.initializeWithDefaults(this)
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@Neum)
            loadKoinModules(REQUIRED_MODULE)
        }
    }

    companion object {
        val REQUIRED_MODULE = listOf(
            NETWORKING_MODULE,
            REPOSITORY_MODULE,
            VIEW_MODEL_MODULE
        )
        lateinit var app: Application

        fun isConnected(): Boolean {
            val connectivityManager =
                app.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null
        }
    }

}
