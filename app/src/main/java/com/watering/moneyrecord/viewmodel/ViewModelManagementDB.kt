package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR


class ViewModelManagementDB(application:Application) : ObservableViewModel(application) {
    var listOfFile: List<String> = listOf()
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.listOfFile)
    }
}