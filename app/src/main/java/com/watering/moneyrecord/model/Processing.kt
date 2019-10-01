package com.watering.moneyrecord.model

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.viewmodel.ViewModelApp
import kotlinx.coroutines.Job
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
            loadingIOKRW(idAccount, selectedDate, false).observeOnce(Observer { io -> io?.let {
                val jobIO = if(io.id == null) insert(io) else update(io)

                runBlocking {
                    jobIO.join()
                    delay(100)
                    dairyKRW(idAccount, selectedDate)
                }
            } })
        }
    }
    fun dairyKRW(idAccount:Int?, selectedDate:String?) {
        viewmodel?.run {
            loadingDairyKRW(idAccount, selectedDate, false).observeOnce(Observer { dairy -> dairy?.let {
                val jobDairyKRW = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyKRW.join()
                    delay(100)

                    getAfterDairyKRW(idAccount, selectedDate).observeOnce(Observer { list ->
                        list?.let {
                            when {
                                list.isNotEmpty() -> list.forEach { date ->
                                    var jobUpdateIO = Job()
                                    var jobUpdateDairy = Job()
                                    loadingIOKRW(idAccount, date, true).observeOnce(Observer { io -> io?.let {
                                        jobUpdateIO = update(io)
                                    } })
                                    loadingDairyKRW(idAccount, date, true).observeOnce(Observer { d -> d?.let {
                                        jobUpdateDairy = update(d)
                                    }})
                                    runBlocking {
                                        jobUpdateIO.join()
                                        jobUpdateDairy.join()
                                        delay(100)
                                        if(date == list.last()) dairyTotal(idAccount, selectedDate)
                                    }
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
            loadingDairyForeign(idAccount, selectedDate, currency, false).observeOnce(Observer { dairy -> dairy?.let {
                val jobDairyForeign = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyForeign.join()
                    delay(100)

                    getAfterDairyForeign(idAccount, selectedDate, currency).observeOnce(Observer { list ->
                        list?.let {
                            when {
                                list.isNotEmpty() -> list.forEach { date ->
                                    var jobUpdateIO = Job()
                                    var jobUpdateDairy = Job()
                                    loadingIOForeign(idAccount, date, currency, true).observeOnce( Observer { io -> io?.let {
                                        jobUpdateIO = update(io)
                                    } })
                                    loadingDairyForeign(idAccount, date, currency, true).observeOnce(Observer { d -> d?.let {
                                        jobUpdateDairy = update(d)

                                    }})
                                    runBlocking {
                                        jobUpdateIO.join()
                                        jobUpdateDairy.join()
                                        delay(100)
                                        if(date == list.last()) dairyTotal(idAccount, selectedDate)
                                    }
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
            loadingDairyTotal(idAccount, selectedDate, false).observeOnce(Observer { dairy -> dairy?.let {
                val jobDairyTotal = if(dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyTotal.join()
                    delay(100)

                    getAfterDairyTotal(idAccount, selectedDate).observeOnce(Observer { list -> list?.let {
                        var jobUpdateHome: Job
                        when {
                            list.isNotEmpty() -> list.forEach { date ->
                                loadingDairyTotal(idAccount, date, true).observeOnce(Observer { d -> d?.let {
                                    val jobUpdateDairyTotal = update(d)
                                    runBlocking {
                                        jobUpdateDairyTotal.join()
                                        delay(100)
                                        if (date == list.last()) {
                                            getHomeByIdAccount(d.account).observeOnce(Observer { home -> home?.let {
                                                home.rate = d.rate
                                                home.principalKRW = d.principalKRW
                                                home.evaluationKRW = d.evaluationKRW
                                                jobUpdateHome = update(home)
                                                runBlocking {
                                                    jobUpdateHome.join()
                                                    delay(100)
                                                    fragmentManager?.popBackStack()
                                                }
                                            } })
                                        }
                                    }
                                } })
                            }
                            else -> {
                                getHomeByIdAccount(idAccount).observeOnce(Observer { home -> home?.let {
                                    home.rate = dairy.rate
                                    home.principalKRW = dairy.principalKRW
                                    home.evaluationKRW = dairy.evaluationKRW
                                    jobUpdateHome = update(home)
                                    runBlocking {
                                        jobUpdateHome.join()
                                        delay(100)
                                        fragmentManager?.popBackStack()
                                    }
                                } })
                            }
                        }
                    } })
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