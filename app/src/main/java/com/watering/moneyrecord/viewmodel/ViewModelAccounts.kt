package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.watering.moneyrecord.BR

class ViewModelAccounts(application:Application) : ObservableViewModel(application) {
    var idAccount = 0

    var indexOfAccount = 0
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        notifyPropertyChanged(BR.indexOfAccount)
    }

    var listOfAccount = MutableLiveData<List<String?>>()
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        notifyPropertyChanged(BR.listOfAccount)
    }
}