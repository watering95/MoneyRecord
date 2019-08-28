package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

import com.watering.moneyrecord.entities.IOForeign

class ViewModelEditInoutForeign(application:Application) : ObservableViewModel(application) {
    var io: IOForeign = IOForeign()

    var date:String? = ""
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.date)
    }

    var deposit: Double? = 0.0
    @Bindable get
        set(value) {
        field = value
        io.input = value
        if(deposit != 0.0) rateDeposit = depositKRW?.div(deposit!!)
        notifyPropertyChanged(BR.rateDeposit)
        notifyPropertyChanged(BR.deposit)
    }

    var depositKRW: Int? = 0
    @Bindable get
        set(value) {
        field = value
        io.inputKRW = value
        if(deposit != 0.0) rateDeposit = depositKRW?.div(deposit!!)
        notifyPropertyChanged(BR.rateDeposit)
        notifyPropertyChanged(BR.depositKRW)
    }

    var withdraw: Double? = 0.0
    @Bindable get
        set(value) {
        field = value
        io.output = value
        if(withdraw != 0.0) rateWithdraw = withdrawKRW?.div(withdraw!!)
        notifyPropertyChanged(BR.rateWithdraw)
        notifyPropertyChanged(BR.withdraw)
    }

    var withdrawKRW: Int? = 0
    @Bindable get
        set(value) {
        field = value
        io.outputKRW = value
        if(withdraw != 0.0) rateWithdraw = withdrawKRW?.div(withdraw!!)
        notifyPropertyChanged(BR.rateWithdraw)
        notifyPropertyChanged(BR.withdrawKRW)
    }

    var principal: Double? = 0.0
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.principal)
    }

    var indexOfCurrency: Int? = 0
    @Bindable get
        set(value) {
        field = value
        io.currency = value
        notifyPropertyChanged(BR.indexOfCurrency)
    }

    var rateDeposit: Double? = 0.0
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.rateDeposit)
    }

    var rateWithdraw: Double? = 0.0
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.rateWithdraw)
    }

    var rateEvaluation: Double? = 0.0
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.rateEvaluation)
    }

    var evaluationKRW: Double? = 0.0
    @Bindable get
        set(value) {
        field = value
        io.evaluationKRW = value
        if(principal != 0.0) rateEvaluation = evaluationKRW?.div(principal!!)
        notifyPropertyChanged(BR.rateEvaluation)
        notifyPropertyChanged(BR.evaluationKRW)
    }
}