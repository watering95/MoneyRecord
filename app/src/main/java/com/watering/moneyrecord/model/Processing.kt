package com.watering.moneyrecord.model

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.watering.moneyrecord.entities.DairyTotal
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.viewmodel.ViewModelApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class Processing(val viewmodel: ViewModelApp?, val fragmentManager: FragmentManager?) {

    fun initHome() {
        viewmodel?.run {
            allAccounts.observeOnce(Observer { listOfAccount -> listOfAccount?.let {
                it.forEach { account ->
                    getHomeByIdAccount(account.id).observeOnce(Observer { home ->
                        when(home) {
                            null -> {
                                getGroup(account.group).observeOnce(Observer { group -> group?.let {
                                    loadingDairyTotal(account.id, MyCalendar.getToday(), false).observeOnce(Observer { dairyTotal -> dairyTotal?.let {
                                        val newHome = Home()
                                        newHome.idAccount = account.id
                                        newHome.group = group.name
                                        newHome.account = account.number
                                        newHome.description = account.institute + " " + account.description
                                        newHome.evaluationKRW = dairyTotal.evaluationKRW
                                        newHome.principalKRW = dairyTotal.principalKRW
                                        newHome.rate = dairyTotal.rate
                                        insert(newHome)
                                    } })
                                } })
                            }
                            else -> {
                                loadingDairyTotal(account.id, MyCalendar.getToday(), false).observeOnce(Observer { dairyTotal -> dairyTotal?.let {
                                    home.evaluationKRW = dairyTotal.evaluationKRW
                                    home.principalKRW = dairyTotal.principalKRW
                                    home.rate = dairyTotal.rate
                                    update(home)
                                } })
                            }
                        }
                    })
                }
            } })
        }
    }

    fun ioKRW(idAccount:Int?, selectedDate:String?) {
        viewmodel?.run {
            loadingIOKRW(idAccount, selectedDate, false).observeOnce( Observer { io -> io?.let {
                val jobIO = if(io.id == null) insert(io) else update(io)

                runBlocking {
                    jobIO.join()
                    checkNextIOKRW(idAccount, selectedDate, selectedDate)
                }
            } })
        }
    }

    private fun checkNextIOKRW(idAccount: Int?, selectedDate: String?, nextDate: String?) {
        viewmodel?.run {
            getNextIOKRW(idAccount, nextDate).observeOnce( Observer { date ->
                if(date.isNullOrEmpty()) dairyKRW(idAccount, selectedDate)
                else {
                    loadingIOKRW(idAccount, date, true).observeOnce( Observer { io -> io?.run {
                        val job = update(io)
                        runBlocking {
                            job.join()
                            checkNextIOKRW(idAccount, selectedDate, date)
                        }
                    } })
                }
            })
        }
    }

    private fun dairyKRW(idAccount:Int?, selectedDate:String?) {
        viewmodel?.run {
            loadingDairyKRW(idAccount, selectedDate, false).observeOnce( Observer { dairy -> dairy?.let {
                val jobDairyKRW = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyKRW.join()
                    checkNextDairyKRW(idAccount, selectedDate, selectedDate)
                }
            } })
        }
    }

    private fun checkNextDairyKRW(idAccount: Int?, selectedDate: String?, nextDate: String?) {
        viewmodel?.run {
            getNextDairyKRW(idAccount, nextDate).observeOnce( Observer { date ->
                if(date.isNullOrEmpty()) dairyTotal(idAccount, selectedDate)
                else {
                    loadingDairyKRW(idAccount, date, true).observeOnce( Observer { dairy -> dairy?.let {
                        val job = update(dairy)
                        runBlocking {
                            job.join()
                            checkNextDairyKRW(idAccount, selectedDate, date)
                        }
                    } })
                }
            })
        }
    }

    fun ioForeign(idAccount:Int?, selectedDate:String?, currency: Int?) {
        viewmodel?.run {
            loadingIOForeign(idAccount, selectedDate, currency,false).observeOnce( Observer { io -> io?.let {
                val jobIO = if(io.id == null) insert(io) else update(io)

                runBlocking {
                    jobIO.join()
                    checkNextIOForeign(idAccount, currency, selectedDate, selectedDate)
                }
            } })
        }
    }

    private fun checkNextIOForeign(idAccount: Int?, currency: Int?, selectedDate: String?, nextDate: String?) {
        viewmodel?.run {
            getNextIOForeign(idAccount, nextDate, currency).observeOnce( Observer { date ->
                if(date.isNullOrEmpty()) dairyForeign(idAccount, selectedDate, currency)
                else {
                    loadingIOForeign(idAccount, date, currency,true).observeOnce( Observer { io -> io?.run {
                        val job = update(io)
                        runBlocking {
                            job.join()
                            checkNextIOForeign(idAccount, currency, selectedDate, date)
                        }
                    } })
                }
            })
        }
    }

    private fun dairyForeign(idAccount:Int?, selectedDate:String?, currency: Int?) {
        viewmodel?.run {
            loadingDairyForeign(idAccount, selectedDate, currency, false).observeOnce(Observer { dairy -> dairy?.let {
                val jobDairyForeign = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyForeign.join()
                    delay(100)
                    checkNextDairyForeign(idAccount, currency, selectedDate, selectedDate)
                }
            } })
        }
    }

    private fun checkNextDairyForeign(idAccount: Int?, currency: Int?, selectedDate: String?, nextDate: String?) {
        viewmodel?.run {
            getNextDairyForeign(idAccount, nextDate, currency).observeOnce( Observer { date ->
                if(date.isNullOrEmpty()) dairyTotal(idAccount, selectedDate)
                else {
                    loadingDairyForeign(idAccount, date, currency, true).observeOnce( Observer { dairy -> dairy?.let {
                        val job = update(dairy)
                        runBlocking {
                            job.join()
                            checkNextDairyForeign(idAccount, currency, selectedDate, date)
                        }
                    } })
                }
            })
        }
    }

    private fun dairyTotal(idAccount:Int?, selectedDate:String?) {
        viewmodel?.run {
            loadingDairyTotal(idAccount, selectedDate, false).observeOnce(Observer { dairy -> dairy?.let {
                val jobDairyTotal = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyTotal.join()
                    updateHome(idAccount, dairy)
                    checkNextDairyTotal(idAccount, selectedDate)
                }
            } })
        }
    }

    private fun checkNextDairyTotal(idAccount: Int?, selectedDate: String?) {
        viewmodel?.run {
            getNextDairyTotal(idAccount, selectedDate).observeOnce( Observer { date ->
                if(date.isNullOrEmpty()) fragmentManager?.popBackStack()
                else {
                    loadingDairyTotal(idAccount, date, true).observeOnce( Observer { dairy -> dairy?.let {
                        val job = update(dairy)
                        runBlocking {
                            job.join()
                            updateHome(idAccount, dairy)
                            checkNextDairyTotal(idAccount, date)
                        }
                    } })
                }
            })
        }
    }

    private fun updateHome(idAccount: Int?, dairy: DairyTotal) {
        viewmodel?.run {
            getHomeByIdAccount(idAccount).observeOnce(Observer { home -> home?.let {
                home.rate = dairy.rate
                home.principalKRW = dairy.principalKRW
                home.evaluationKRW = dairy.evaluationKRW
                update(home)
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