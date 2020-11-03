package com.watering.moneyrecord.fragments

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.model.Processing
import com.watering.moneyrecord.viewmodel.ViewModelApp

open class ParentFragment: Fragment() {
    val activity by lazy { getActivity() as MainActivity }
    val application: Application? by lazy { activity.application }
    val mViewModel by lazy { ViewModelApp(activity.application) }
    val mFragmentManager by lazy { activity.supportFragmentManager }
    val mActionBar by lazy { activity.supportActionBar }
    val processing by lazy { application?.let { Processing(it, mFragmentManager) } }

    fun replaceFragment(fragment: Fragment) {
        mFragmentManager.run {
            val transaction = beginTransaction()
            transaction.replace(R.id.frame_main, fragment).addToBackStack(null).commit()
        }
    }

    fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object: Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}