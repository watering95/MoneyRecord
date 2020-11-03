package com.watering.moneyrecord.viewmodel

import android.app.Application

class ViewModelManagementCategorySub(application:Application) : ObservableViewModel(application) {
    fun getCatMainByName(name: String?) = repository.getCatMainByName(name)  // FragmentManagementCategorySub
}