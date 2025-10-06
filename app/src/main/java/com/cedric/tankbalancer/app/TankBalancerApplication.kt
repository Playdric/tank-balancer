package com.cedric.tankbalancer.app

import android.app.Application
import com.cedric.tankbalancer.data.di.dataModule
import com.cedric.tankbalancer.domain.di.domainModule
import com.cedric.tankbalancer.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class TankBalancerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.Forest.plant(
            tree = Timber.DebugTree()
        )

        startKoin {
            androidLogger()
            androidContext(this@TankBalancerApplication)
            modules(
                presentationModule,
                dataModule,
                domainModule
            )
        }
    }
}
