package com.watering.moneyrecord.viewmodel

import android.app.Application

class ViewModelBookIncome(application:Application) : ObservableViewModel(application) {
    fun getIncomes(date: String?) = repository.getIncomes(date)  // FragmentBookIncome
}