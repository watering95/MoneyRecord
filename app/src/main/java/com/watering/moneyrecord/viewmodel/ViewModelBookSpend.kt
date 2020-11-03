package com.watering.moneyrecord.viewmodel

import android.app.Application

class ViewModelBookSpend(application:Application) : ObservableViewModel(application) {
    fun getSpends(date: String?) = repository.getSpends(date)  // FragmentBookSpend
}