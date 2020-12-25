package com.watering.moneyrecord.model

import android.app.Application
import androidx.annotation.WorkerThread
import com.watering.moneyrecord.entities.*
import kotlinx.coroutines.CoroutineScope

class AppRepository(val application: Application, private val scope: CoroutineScope) {
    var db = AppDatabase.getDatabase(application, scope)
    private var daoGroup = db.daoGroup()
    private var daoAccount = db.daoAccount()
    private var daoCatMain = db.daoCategoryMain()
    private var daoCatSub = db.daoCategorySub()
    private var daoCard = db.daoCard()
    private var daoSpend = db.daoSpend()
    private var daoIncome = db.daoIncome()
    private var daoSpendCash = db.daoSpendCash()
    private var daoSpendCard = db.daoSpendCard()
    private var daoIOKRW = db.daoIOKRW()
    private var daoIOForeign = db.daoIOForeign()
    private var daoDairyKRW = db.daoDairyKRW()
    private var daoDairyForeign = db.daoDairyForeign()
    private var daoDairyTotal = db.daoDairyTotal()
    private var daoHome = db.daoHome()

    var allGroups = daoGroup.getAll()
    var allAccounts = daoAccount.getAll()
    var allCatMains = daoCatMain.getAll()
    var allCatSubs = daoCatSub.getAll()
    var allCards = daoCard.getAll()
    var allHomes = daoHome.getAll()
    var allSpendCashes = daoSpendCash.getAll()

    fun initialize() {
        db = AppDatabase.getDatabase(application, scope)
        daoGroup = db.daoGroup()
        daoAccount = db.daoAccount()
        daoCatMain = db.daoCategoryMain()
        daoCatSub = db.daoCategorySub()
        daoCard = db.daoCard()
        daoSpend = db.daoSpend()
        daoIncome = db.daoIncome()
        daoSpendCash = db.daoSpendCash()
        daoSpendCard = db.daoSpendCard()
        daoIOKRW = db.daoIOKRW()
        daoIOForeign = db.daoIOForeign()
        daoDairyKRW = db.daoDairyKRW()
        daoDairyForeign = db.daoDairyForeign()
        daoDairyTotal = db.daoDairyTotal()

        allGroups = daoGroup.getAll()
        allAccounts = daoAccount.getAll()
        allCatMains = daoCatMain.getAll()
        allCatSubs = daoCatSub.getAll()
        allCards = daoCard.getAll()
        allHomes = daoHome.getAll()
    }

    fun getFirstDate(group: Int?) = daoDairyTotal.getFirstDate(group)
    fun getFirstDate() = daoDairyTotal.getFirstDate()

    fun getGroup(id: Int?) = daoGroup.get(id)
    fun getGroup(name: String?) = daoGroup.get(name)

    fun getAccount(id: Int?) = daoAccount.get(id)
    fun getAccount(number: String?) = daoAccount.get(number)
    fun getAccountByCode(code: String?) = daoAccount.getByCode(code)

    fun getHomeByIdAccount(id_account: Int?) = daoHome.getByIdAccount(id_account)
    fun getHomesByGroup(group: String?) = daoHome.getByGroup(group)

    fun getCatMain(id: Int?) = daoCatMain.get(id)
    fun getCatMain(kind: String?) = daoCatMain.get(kind)
    fun getCatMainBySub(id_sub: Int?) = daoCatMain.getBySub(id_sub)
    fun getCatMainByName(name: String?) = daoCatMain.getByName(name)

    fun getCardByCode(code: String?) = daoCard.getByCode(code)
    fun getCardByNumber(number: String?) = daoCard.getByNumber(number)

    fun getIncomes(date: String?) = daoIncome.get(date)

    fun getCatSub(id: Int?) = daoCatSub.get(id)
    fun getCatSub(nameOfSub: String?, nameOfMain: String?) = daoCatSub.get(nameOfSub, nameOfMain)
    fun getCatSubsByMain(nameOfMain: String?) = daoCatSub.getByMain(nameOfMain)

    fun getSpends(date: String?) = daoSpend.get(date)
    fun getSpendCash(code: String?) = daoSpendCash.get(code)
    fun getSpendCard(code: String?) = daoSpendCard.get(code)

    fun getIOKRW(id_account: Int?, date: String?) = daoIOKRW.get(id_account, date)
    fun getIOForeign(id_account: Int?, date: String?, currency: Int?) = daoIOForeign.get(id_account, date, currency)

    fun getDairyKRW(id_account: Int?, date: String?) = daoDairyKRW.get(id_account, date)
    fun getDairyForeign(id_account: Int?, date: String?, currency: Int?) = daoDairyForeign.get(id_account, date, currency)
    fun getDairyTotal(id_account: Int?, date: String?) = daoDairyTotal.get(id_account, date)

    fun getDairyTotalOrderByDate(id_account: Int?) = daoDairyTotal.getOrderByDate(id_account)

    fun getNextIOKRW(id_account: Int?, date: String?) = daoIOKRW.getNext(id_account, date)
    fun getNextIOForeign(id_account: Int?, date: String?, currency: Int?) = daoIOForeign.getNext(id_account, date, currency)
    fun getNextDairyKRW(id_account: Int?, date: String?) = daoDairyKRW.getNext(id_account, date)
    fun getNextDairyForeign(id_account: Int?, date: String?, currency: Int?) = daoDairyForeign.getNext(id_account, date, currency)
    fun getNextDairyTotal(id_account: Int?, date: String?) = daoDairyTotal.getNext(id_account, date)

    fun getLastSpendCode(date: String?) = daoSpend.getLastCode(date)
    fun getLastIOKRW(id_account: Int?, date: String?) = daoIOKRW.getLast(id_account, date)
    fun getLastIOForeign(id_account: Int?, date: String?, currency: Int?) = daoIOForeign.getLast(id_account, date, currency)
    fun getLastDairyKRW(id_account: Int?, date: String?) = daoDairyKRW.getLast(id_account, date)
    fun getLastIOForeign(id_account: Int?, date: String?) = daoIOForeign.getLast(id_account, date)
    fun getLastDairyForeign(id_account: Int?, date: String?) = daoDairyForeign.getLast(id_account, date)

    fun sumOfSpendCashForDate(id_account: Int?, date: String?) = daoSpend.sumOfSpendCash(id_account, date)
    fun sumOfSpendCardForDate(id_account: Int?, date: String?) = daoSpend.sumOfSpendCard(id_account, date)
    fun sumOfIncomeForDate(id_account: Int?, date: String?) = daoIncome.sum(id_account, date)

    fun sumOfInputOfKRWUntilDate(id_account: Int?, date: String?) = daoIOKRW.sumOfInput(id_account, date)
    fun sumOfInputOfForeignUntilDate(id_account: Int?, date: String?, currency: Int?) = daoIOForeign.sumOfInput(id_account, date, currency)
    fun sumOfInputKRWOfForeignUntilDate(id_account: Int?, date: String?, currency: Int?) = daoIOForeign.sumOfInputKRW(id_account, date, currency)
    fun sumOfOutputOfKRWUntilDate(id_account: Int?, date: String?) = daoIOKRW.sumOfOutput(id_account, date)
    fun sumOfOutputOfForeignUntilDate(id_account: Int?, date: String?, currency: Int?) = daoIOForeign.sumOfOutput(id_account, date, currency)
    fun sumOfOutputKRWOfForeignUntilDate(id_account: Int?, date: String?, currency: Int?) = daoIOForeign.sumOfOutputKRW(id_account, date, currency)
    fun sumOfIncomeUntilDate(id_account: Int?, date: String?) = daoIOKRW.sumOfIncome(id_account, date)
    fun sumOfSpendCardUntilDate(id_account: Int?, date: String?) = daoIOKRW.sumOfSpendCard(id_account, date)
    fun sumOfSpendCashUntilDate(id_account: Int?, date: String?) = daoIOKRW.sumOfSpendCash(id_account, date)

    fun sumOfIncome(startDate: String?, endDate: String?) = daoIOKRW.sumOfIncome(startDate, endDate)
    fun sumOfSpend(startDate: String?, endDate: String?) = daoIOKRW.sumOfSpend(startDate, endDate)
    fun sumOfMonthlyStatistics() = daoIOKRW.sumOfMonthly()

    fun sumOfEvaluation(group: Int?, date: String) = daoDairyTotal.getSumOfEvaluation(group, date)
    fun sumOfEvaluation(date: String) = daoDairyTotal.getSumOfEvaluation(date)
    fun sumOfPrincipal(group: Int?, date: String) = daoDairyTotal.getSumOfPrincipal(group, date)
    fun sumOfPrincipal(date: String) = daoDairyTotal.getSumOfPrincipal(date)

    fun close() {
        if(db.isOpen) db.openHelper.close()
    }

    @WorkerThread
    fun <T> insert(t: T) {
        if(t is Group) daoGroup.insert(t)
        if(t is Account) daoAccount.insert(t)
        if(t is CategoryMain) daoCatMain.insert(t)
        if(t is CategorySub) daoCatSub.insert(t)
        if(t is Card) daoCard.insert(t)
        if(t is Income) daoIncome.insert(t)
        if(t is Spend) daoSpend.insert(t)
        if(t is SpendCash) daoSpendCash.insert(t)
        if(t is SpendCard) daoSpendCard.insert(t)
        if(t is IOKRW) daoIOKRW.insert(t)
        if(t is IOForeign) daoIOForeign.insert(t)
        if(t is DairyKRW) daoDairyKRW.insert(t)
        if(t is DairyForeign) daoDairyForeign.insert(t)
        if(t is DairyTotal) daoDairyTotal.insert(t)
        if(t is Home) daoHome.insert(t)
    }

    @WorkerThread
    fun <T> update(t: T) {
        if(t is Group) daoGroup.update(t)
        if(t is Account) daoAccount.update(t)
        if(t is CategoryMain) daoCatMain.update(t)
        if(t is CategorySub) daoCatSub.update(t)
        if(t is Card) daoCard.update(t)
        if(t is Income) daoIncome.update(t)
        if(t is Spend) daoSpend.update(t)
        if(t is SpendCash) daoSpendCash.update(t)
        if(t is SpendCard) daoSpendCard.update(t)
        if(t is IOKRW) daoIOKRW.update(t)
        if(t is IOForeign) daoIOForeign.update(t)
        if(t is DairyKRW) daoDairyKRW.update(t)
        if(t is DairyForeign) daoDairyForeign.update(t)
        if(t is DairyTotal) daoDairyTotal.update(t)
        if(t is Home) daoHome.update(t)
    }

    @WorkerThread
    fun <T> delete(t: T) {
        if(t is Group) daoGroup.delete(t)
        if(t is Account) daoAccount.delete(t)
        if(t is CategoryMain) daoCatMain.delete(t)
        if(t is CategorySub) daoCatSub.delete(t)
        if(t is Card) daoCard.delete(t)
        if(t is Income) daoIncome.delete(t)
        if(t is Spend) daoSpend.delete(t)
        if(t is SpendCash) daoSpendCash.delete(t)
        if(t is SpendCard) daoSpendCard.delete(t)
        if(t is IOKRW) daoIOKRW.delete(t)
        if(t is IOForeign) daoIOForeign.delete(t)
        if(t is DairyKRW) daoDairyKRW.delete(t)
        if(t is DairyForeign) daoDairyForeign.delete(t)
        if(t is DairyTotal) daoDairyTotal.delete(t)
        if(t is Home) daoHome.delete(t)
    }
}