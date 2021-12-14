package com.choco_tyranno.team_tree.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {
    private val progressContentName = MutableLiveData<String>("name")

    fun getProgressContentName() = progressContentName
    fun setProgressContentName(contentName: String) {
        progressContentName.value = contentName
    }
}