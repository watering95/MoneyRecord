package com.watering.moneyrecord.viewmodel

import android.app.Application

class ViewModelBookStatistics(application:Application) : ObservableViewModel(application) {
    fun sumOfMonthlyStatistics() = repository.sumOfMonthlyStatistics()  // FragmentBookStatistics
}