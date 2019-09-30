package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

import com.watering.moneyrecord.entities.Income

class ViewModelEditIncome(application: Application) : ObservableViewModel(application) {
    var idAccount:Int? = 0

    var income: Income = Income()
    @Bindable get
        set(value) {
        field = value

        notifyPropertyChanged(BR.income)
    }

    var listOfMain = Transformations.map(getCatMainsByKind("income")) { list -> list.map { it.name } } as MutableLiveData<List<String?>>
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.listOfMain)
    }

    var indexOfMain: Int = -1
    @Bindable get
        set(value) {
        field = value
        listOfSub = Transformations.switchMap(listOfMain) { listOfMain ->
            Transformations.map(getCatSubsByMain(listOfMain[field])) { listOfSub -> listOfSub.map { it.name } }
        } as MutableLiveData<List<String?>>
        indexOfSub = 0
        notifyPropertyChanged(BR.indexOfMain)
    }

    var listOfSub = MutableLiveData<List<String?>> ()
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.listOfSub)
    }

    var indexOfSub: Int = -1
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.indexOfSub)
    }

    var listOfAccount = Transformations.map(allAccounts) { list -> list.map { it.number + " " + it.institute + " " + it.description } } as MutableLiveData<List<String>>
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.listOfAccount)
    }

    var indexOfAccount: Int = -1
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.indexOfAccount)
    }
}