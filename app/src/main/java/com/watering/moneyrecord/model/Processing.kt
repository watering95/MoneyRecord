package com.watering.moneyrecord.model

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.watering.moneyrecord.viewmodel.ViewModelApp
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking

class Processing(val viewmodel: ViewModelApp?, val fragmentManager: FragmentManager?) {

    fun ioKRW(idAccount:Int?, selectedDate:String?) {
        viewmodel?.run {
            loadingIOKRW(idAccount, selectedDate).observeOnce(Observer { io -> io?.let {
                val jobIO = if(io.id == null) insert(io) else update(io)

                runBlocking {
                    jobIO.cancelAndJoin()
                    dairyKRW(idAccount, selectedDate)
                }
            } })
        }
    }
    fun dairyKRW(idAccount:Int?, selectedDate:String?) {
        viewmodel?.run {
            loadingDairyKRW(idAccount, selectedDate).observeOnce(Observer { dairy -> dairy?.let {
                val jobDairyKRW = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyKRW.cancelAndJoin()

                    getAfterDairyKRW(idAccount, selectedDate).observeOnce(Observer { list ->
                        list?.let {
                            when {
                                list.isNotEmpty() -> list.forEach { date ->
                                    loadingDairyKRW(idAccount, date).observeOnce(Observer { d -> d?.let {
                                        runBlocking {
                                            update(d).cancelAndJoin()
                                            if(date == list.last()) dairyTotal(idAccount, selectedDate)
                                        }
                                    }})
                                }
                                else -> dairyTotal(idAccount, selectedDate)
                            }
                        }
                    })
                }
            } })
        }
    }
    fun dairyForeign(idAccount:Int?, selectedDate:String?, currency: Int?) {
        viewmodel?.run {
            loadingDairyForeign(idAccount, selectedDate, currency).observeOnce(Observer { dairy -> dairy?.let {
                val jobDairyForeign = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyForeign.cancelAndJoin()

                    getAfterDairyForeign(idAccount, selectedDate, currency).observeOnce(Observer { list ->
                        list?.let {
                            when {
                                list.isNotEmpty() -> list.forEach { date ->
                                    loadingDairyForeign(idAccount, date, currency).observeOnce(Observer { d -> d?.let {
                                        runBlocking {
                                            update(d).cancelAndJoin()
                                            if(date == list.last()) dairyTotal(idAccount, selectedDate)
                                        }
                                    }})
                                }
                                else -> dairyTotal(idAccount, selectedDate)
                            }
                        }
                    })

                    dairyTotal(idAccount, selectedDate)
                }
            } })
        }
    }
    private fun dairyTotal(idAccount:Int?, selectedDate:String?) {
        viewmodel?.run {
            loadingDairyTotal(idAccount, selectedDate).observeOnce(Observer { dairy -> dairy?.let {
                val jobDairyTotal = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyTotal.cancelAndJoin()

                    getAfterDairyTotal(idAccount, selectedDate).observeOnce(Observer { list ->
                        list?.let {
                            when {
                                list.isNotEmpty() -> list.forEach { date ->
                                    loadingDairyTotal(idAccount, date).observeOnce(Observer { d -> d?.let {
                                        runBlocking {
                                            update(d).cancelAndJoin()
                                            if (date == list.last()) fragmentManager?.popBackStack()
                                        }
                                    } })
                                }
                                else -> fragmentManager?.popBackStack()
                            }
                        }
                    })
                }
            } })
        }
    }
    private fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object: Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}