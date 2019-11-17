package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class ViewModelAccountTransfer(application:Application) : ObservableViewModel(application) {
    var listOfAccount  = Transformations.map(allAccounts) { list -> list.map { it.number + " " + it.institute + " " + it.description } } as MutableLiveData<List<String>>
    @Bindable get
    set(value) {
        field = value
        notifyPropertyChanged(BR.listOfAccount)
    }

    var indexOfDepositAccount: Int = -1
    @Bindable get
    set(value) {
        field = value
        notifyPropertyChanged(BR.indexOfDepositAccount)
    }

    var indexOfWithdrawAccount: Int = -1
    @Bindable get
    set(value) {
        field = value
        notifyPropertyChanged(BR.indexOfWithdrawAccount)
    }
}