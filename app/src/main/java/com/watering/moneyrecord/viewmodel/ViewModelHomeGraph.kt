package com.watering.moneyrecord.viewmodel

import android.app.Application

class ViewModelHomeGraph(application:Application) : ObservableViewModel(application) {
    fun sumOfEvaluation(date: String) = if(currentGroupId > 0) repository.sumOfEvaluation(currentGroupId, date) else repository.sumOfEvaluation(date)  // FragmentHomeGraph

    fun sumOfPrincipal(date: String) = if(currentGroupId > 0) repository.sumOfPrincipal(currentGroupId, date) else repository.sumOfPrincipal(date)  // FragmentHomeGraph
}