package com.choco_tyranno.team_tree.di.module

import com.choco_tyranno.team_tree.presentation.card_rv.listener.OnClickListenerForCallBtn
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
object MainCardActivityModule {

    @Provides
    fun provideOnClickListenerForCallBtn() : OnClickListenerForCallBtn = OnClickListenerForCallBtn.getInstance()
}