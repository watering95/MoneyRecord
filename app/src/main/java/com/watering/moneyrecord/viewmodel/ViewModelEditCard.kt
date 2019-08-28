package com.watering.moneyrecord.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.watering.moneyrecord.entities.Card

class ViewModelEditCard(@Bindable var card: Card?, @Bindable var selected:Int = -1) : BaseObservable()