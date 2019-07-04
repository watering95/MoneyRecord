package com.watering.moneyrecord.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.watering.moneyrecord.entities.Account

class ViewModelEditAccount(@Bindable var account:Account?, @Bindable var selected:Int?) : BaseObservable()