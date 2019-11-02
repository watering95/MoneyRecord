package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class ViewModelBook(application: Application) : ObservableViewModel(application) {
    var totalIncome: Int = 0
        @Bindable get
        set(value) {
            field = value

            notifyPropertyChanged(BR.totalIncome)
        }

    var totalSpend: Int = 0
        @Bindable get
        set(value) {
            field = value

            notifyPropertyChanged(BR.totalSpend)
        }
}