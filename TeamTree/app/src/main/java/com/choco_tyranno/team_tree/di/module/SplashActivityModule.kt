package com.choco_tyranno.team_tree.di.module

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ActivityComponent::class)
@Module
object SplashActivityModule {

    @Provides
    fun provideAppUpdateManager(@ApplicationContext context : Context) : AppUpdateManager {
        return AppUpdateManagerFactory.create(context);
    }

}