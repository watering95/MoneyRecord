package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.*


class ViewModelHome(application:Application) : ObservableViewModel(application) {
    var totalPrincipal: Int = 0
    @Bindable get
        set(value) {
        field = value
        if(totalEvaluation != 0) totalRate = totalEvaluation.toDouble() / totalPrincipal * 100 - 100
        notifyPropertyChanged(BR.totalPrincipal)
    }

    var totalEvaluation: Int = 0
    @Bindable get
        set(value) {
        field = value
        if(totalEvaluation != 0) totalRate = totalEvaluation.toDouble() / totalPrincipal * 100 - 100
        notifyPropertyChanged(BR.totalEvaluation)
    }

    var totalRate: Double = 0.0
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.totalRate)
    }

    var listOfGroup: LiveData<List<String?>> = Transformations.map(allGroups) { list -> listOf("전체") + list.map { it.name } }
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.listOfGroup)
    }

    var indexOfGroup = 0
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.indexOfGroup)
    }
}