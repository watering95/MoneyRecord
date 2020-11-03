package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.*


class ViewModelAccounts(application:Application) : ObservableViewModel(application) {
    var idAccount = 0

    var indexOfAccount = -1
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.indexOfAccount)
    }

    var listOfAccount = MutableLiveData<List<String?>>()
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.listOfAccount)
    }

    fun getDairyTotalOrderByDate(idAccount: Int?) = repository.getDairyTotalOrderByDate(idAccount)  // FragmentAccounts
}