package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.*

import com.watering.moneyrecord.entities.Spend

class ViewModelEditSpend(application:Application) : ObservableViewModel(application) {
    var oldCode:String = "000000000000"
    var newCode:String = oldCode
    var idAccount:Int? = 0
    var idCard:Int? = 0

    var listOfMain = Transformations.map(getCatMainsByKind("spend")) { list -> list.map { it.name } }
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.listOfMain)
    }

    var spend: Spend = Spend()
    @Bindable get
        set(value) {
        field = value

        value.apply {
            newCode = newCode.replaceRange(2,10, date?.removeRange(4,5)?.removeRange(6,7).toString())
        }

        notifyPropertyChanged(BR.spend)
    }

    var indexOfMain: Int = -1
    @Bindable get
        set(value) {
        field = value
        listOfSub = Transformations.switchMap(listOfMain) { listOfMain ->
            Transformations.map(getCatSubsByMain(listOfMain[field])) { listOfSub -> listOfSub.map { it.name } }
        } as MutableLiveData<List<String?>>
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

    var listOfPay2 = MutableLiveData<List<String>> ()
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.listOfPay2)
    }

    var indexOfPay1:Int = -1
    @Bindable get
        set(value) {
        field = value
        newCode = newCode.replaceRange(0,1,"${field+1}")
        when(field) {
            0 -> listOfPay2 = Transformations.map(allAccounts) { list -> list.map { it.number + " " + it.institute + " " + it.description }} as MutableLiveData<List<String>>
            1 -> listOfPay2 = Transformations.map(allCards) { list -> list.map { it.number + " " + it.company + " " + it.name} } as MutableLiveData<List<String>>
        }
        notifyPropertyChanged(BR.indexOfPay1)
    }

    var indexOfPay2:Int = -1
    @Bindable get
        set(value) {
        field = value
        notifyPropertyChanged(BR.indexOfPay2)
    }

    fun getAccountByCode(code: String?) = repository.getAccountByCode(code)  // FragmentEditSpend
    fun getSpendCash(code: String?) = repository.getSpendCash(code)  // FragmentEditSpend
    fun getSpendCard(code: String?) = repository.getSpendCard(code)  // FragmentEditSpend
    fun getCardByCode(code: String?) = repository.getCardByCode(code)  // FragmentEditSpend
    fun getCardByNumber(number: String?) = repository.getCardByNumber(number)  // FragmentEditSpend
    fun getLastSpendCode(date: String?) = repository.getLastSpendCode(date)  // FragmentEditSpend
}