package com.watering.moneyrecord.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.watering.moneyrecord.entities.CategorySub

class ViewModelEditCategorySub(@Bindable var categorySub: CategorySub?, @Bindable var selected:Int = -1) : BaseObservable()