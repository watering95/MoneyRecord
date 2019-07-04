package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import com.watering.moneyrecord.BR
import com.watering.moneyrecord.entities.IOKRW

class ViewModelEditInoutKRW(application:Application) : ObservableViewModel(application) {
    var io:IOKRW = IOKRW()

    var date:String? = ""
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        io.date = value
        notifyPropertyChanged(BR.date)
    }

    var principal: Int? = 0
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        notifyPropertyChanged(BR.principal)
    }

    var withdraw:Int? = 0
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        io.output = value
        notifyPropertyChanged(BR.withdraw)
    }

    var evaluation:Int? = 0
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        io.evaluationKRW = value
        notifyPropertyChanged(BR.evaluation)
    }

    var spend:Int? = 0
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        notifyPropertyChanged(BR.spend)
    }

    var income:Int? = 0
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        notifyPropertyChanged(BR.income)
    }

    var deposit:Int? = 0
    @Bindable get() {
        return field
    }
    set(value) {
        field = value
        io.input = value
        notifyPropertyChanged(BR.deposit)
    }
}