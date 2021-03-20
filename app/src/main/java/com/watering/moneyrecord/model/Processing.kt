package com.watering.moneyrecord.model

import android.app.Application
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.watering.moneyrecord.entities.DairyTotal
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.viewmodel.ObservableViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class Processing(application: Application, private val fragmentManager: FragmentManager?): ObservableViewModel(application) {

    fun initHome() {
        allAccounts.observeOnce { listOfAccount ->
            listOfAccount?.let {
                it.forEach { account ->
                    getHomeByIdAccount(account.id).observeOnce { home ->
                        when (home) {
                            null -> {
                                getGroup(account.group).observeOnce { group -> group?.let {
                                    loadingDairyTotal(account.id, MyCalendar.getToday(),false).observeOnce { dairyTotal -> dairyTotal?.let {
                                        val newHome = Home()
                                        newHome.idAccount = account.id
                                        newHome.group = group.name
                                        newHome.account = account.number
                                        newHome.description = account.institute + " " + account.description
                                        newHome.evaluationKRW = dairyTotal.evaluationKRW
                                        newHome.principalKRW = dairyTotal.principalKRW
                                        newHome.rate = dairyTotal.rate
                                        insert(newHome)
                                    } }
                                } }
                            }
                            else -> {
                                loadingDairyTotal(
                                    account.id, MyCalendar.getToday(), false).observeOnce { dairyTotal -> dairyTotal?.let {
                                        home.evaluationKRW = dairyTotal.evaluationKRW
                                        home.principalKRW = dairyTotal.principalKRW
                                        home.rate = dairyTotal.rate
                                        update(home)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }  // FragmentHome
    fun ioKRW(idAccount:Int?, selectedDate:String?, isUpdateEvaluation: Boolean = false) {
        loadingIOKRW(idAccount, selectedDate, isUpdateEvaluation).observeOnce { io ->
            io?.let {
                val jobIO = if (io.id == null) insert(io) else update(io)

                runBlocking {
                    jobIO.join()
                    delay(100)
                    checkNextIOKRW(idAccount, selectedDate, selectedDate, isUpdateEvaluation)
                }
            }
        }
    }  // FragmentAccountTransfer, FragmentEditIncome, FragmentEditInoutKRW, FragmentEditSpend
    fun ioForeign(idAccount:Int?, selectedDate:String?, currency: Int?) {
        loadingIOForeign(idAccount, selectedDate, currency,false).observeOnce { io ->
            io?.let {
                val jobIO = if (io.id == null) insert(io) else update(io)

                runBlocking {
                    jobIO.join()
                    delay(100)
                    checkNextIOForeign(idAccount, currency, selectedDate, selectedDate)
                }
            }
        }
    }  // FragmentEditInoutForeign

    private fun checkNextIOKRW(idAccount: Int?, selectedDate: String?, nextDate: String?, isUpdateEvaluation: Boolean = false) {
        getNextIOKRW(idAccount, nextDate).observeOnce { date ->
            if (date.isNullOrEmpty()) dairyKRW(idAccount, selectedDate, isUpdateEvaluation)
            else {
                loadingIOKRW(idAccount, date, true).observeOnce { io ->
                    io?.run {
                        val job = update(io)
                        runBlocking {
                            job.join()
                            delay(100)
                            checkNextIOKRW(idAccount, selectedDate, date, isUpdateEvaluation)
                        }
                    }
                }
            }
        }
    }
    private fun dairyKRW(idAccount:Int?, selectedDate:String?, isUpdateEvaluation: Boolean = false) {
        loadingDairyKRW(idAccount, selectedDate, isUpdateEvaluation).observeOnce { dairy ->
            dairy?.let {
                val jobDairyKRW = if (dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyKRW.join()
                    delay(100)
                    checkNextDairyKRW(idAccount, selectedDate, selectedDate, isUpdateEvaluation)
                }
            }
        }
    }
    private fun checkNextDairyKRW(idAccount: Int?, selectedDate: String?, nextDate: String?, isUpdateEvaluation: Boolean = false) {
        getNextDairyKRW(idAccount, nextDate).observeOnce { date ->
            if (date.isNullOrEmpty()) dairyTotal(idAccount, selectedDate, isUpdateEvaluation)
            else {
                loadingDairyKRW(idAccount, date, true).observeOnce { dairy ->
                    dairy?.let {
                        val job = update(dairy)
                        runBlocking {
                            job.join()
                            delay(100)
                            checkNextDairyKRW(idAccount, selectedDate, date, isUpdateEvaluation)
                        }
                    }
                }
            }
        }
    }

    private fun checkNextIOForeign(idAccount: Int?, currency: Int?, selectedDate: String?, nextDate: String?) {
        getNextIOForeign(idAccount, nextDate, currency).observeOnce { date ->
            if (date.isNullOrEmpty()) dairyForeign(idAccount, selectedDate, currency)
            else {
                loadingIOForeign(idAccount, date, currency, true).observeOnce { io ->
                    io?.run {
                        val job = update(io)
                        runBlocking {
                            job.join()
                            delay(100)
                            checkNextIOForeign(idAccount, currency, selectedDate, date)
                        }
                    }
                }
            }
        }
    }
    private fun dairyForeign(idAccount:Int?, selectedDate:String?, currency: Int?) {
        loadingDairyForeign(idAccount, selectedDate, currency, false).observeOnce { dairy ->
            dairy?.let {
                val jobDairyForeign = if (dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyForeign.join()
                    delay(100)
                    checkNextDairyForeign(idAccount, currency, selectedDate, selectedDate)
                }
            }
        }
    }
    private fun checkNextDairyForeign(idAccount: Int?, currency: Int?, selectedDate: String?, nextDate: String?) {
        getNextDairyForeign(idAccount, nextDate, currency).observeOnce { date ->
            if (date.isNullOrEmpty()) dairyTotal(idAccount, selectedDate)
            else {
                loadingDairyForeign(idAccount, date, currency, true).observeOnce { dairy ->
                    dairy?.let {
                        val job = update(dairy)
                        runBlocking {
                            job.join()
                            delay(100)
                            checkNextDairyForeign(idAccount, currency, selectedDate, date)
                        }
                    }
                }
            }
        }
    }
    private fun dairyTotal(idAccount:Int?, selectedDate:String?, isUpdateEvaluation: Boolean = false) {
        loadingDairyTotal(idAccount, selectedDate, isUpdateEvaluation).observeOnce { dairy ->
            dairy?.let {
                val jobDairyTotal = if (dairy.id == null) insert(dairy) else update(dairy)

                runBlocking {
                    jobDairyTotal.join()
                    delay(100)
                    updateHome(idAccount, dairy)
                    checkNextDairyTotal(idAccount, selectedDate)
                }
            }
        }
    }
    private fun checkNextDairyTotal(idAccount: Int?, selectedDate: String?) {
        getNextDairyTotal(idAccount, selectedDate).observeOnce { date ->
            if (date.isNullOrEmpty()) fragmentManager?.popBackStack()
            else {
                loadingDairyTotal(idAccount, date, true).observeOnce { dairy ->
                    dairy?.let {
                        val job = update(dairy)
                        runBlocking {
                            job.join()
                            delay(100)
                            updateHome(idAccount, dairy)
                            checkNextDairyTotal(idAccount, date)
                        }
                    }
                }
            }
        }
    }
    private fun updateHome(idAccount: Int?, dairy: DairyTotal) {
        getHomeByIdAccount(idAccount).observeOnce { home ->
            home?.let {
                home.rate = dairy.rate
                home.principalKRW = dairy.principalKRW
                home.evaluationKRW = dairy.evaluationKRW
                update(home)
            }
        }
    }

    private fun getNextIOKRW(idAccount: Int?, date: String?) = repository.getNextIOKRW(idAccount, date)
    private fun getNextIOForeign(idAccount: Int?, date: String?, currency: Int?) = repository.getNextIOForeign(idAccount, date, currency)
    private fun getNextDairyKRW(idAccount: Int?, date: String?) = repository.getNextDairyKRW(idAccount, date)
    private fun getNextDairyForeign(idAccount: Int?, date: String?, currency: Int?) = repository.getNextDairyForeign(idAccount, date, currency)
    private fun getNextDairyTotal(idAccount: Int?, date: String?) = repository.getNextDairyTotal(idAccount, date)

    private fun getLastDairyForeign(idAccount: Int?, date: String?) = repository.getLastDairyForeign(idAccount, date)
    private fun getLastIOForeign(idAccount: Int?, date: String?) = repository.getLastIOForeign(idAccount, date)
    private fun getDairyTotal(idAccount: Int?, date: String?) = repository.getDairyTotal(idAccount, date)

    private fun loadingDairyTotal(idAccount: Int?, date: String?, isUpdateEvaluation: Boolean): LiveData<DairyTotal> {
        return Transformations.switchMap(getLastDairyForeign(idAccount, date)) { listOf_last_dairy_foreign ->
            Transformations.switchMap(getLastIOForeign(idAccount, date)) { listOf_last_io_foreign ->
                Transformations.switchMap(loadingDairyKRW(idAccount, date, isUpdateEvaluation)) { dairy_krw ->
                    Transformations.switchMap(loadingIOKRW(idAccount, date, isUpdateEvaluation)) { io_krw ->
                        Transformations.map(getDairyTotal(idAccount, date)) { dairy_total ->
                            var principal = 0
                            var evaluation = 0
                            var rate = 0.0

                            dairy_krw?.run { principal = principalKRW!! }
                            io_krw?.run { evaluation = evaluationKRW!! }

                            listOf_last_dairy_foreign.forEach { principal += it.principalKRW!! }
                            listOf_last_io_foreign.forEach { evaluation += it.evaluationKRW!!.toInt() }

                            if (principal != 0 && evaluation != 0) rate = evaluation.toDouble() / principal * 100 - 100

                            if (dairy_total == null) {
                                val new = DairyTotal()
                                new.account = idAccount
                                new.date = date
                                new.evaluationKRW = evaluation
                                new.principalKRW = principal
                                new.rate = rate
                                new
                            } else {
                                dairy_total.evaluationKRW = evaluation
                                dairy_total.principalKRW = principal
                                dairy_total.rate = rate
                                dairy_total
                            }
                        }
                    }
                }
            }
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