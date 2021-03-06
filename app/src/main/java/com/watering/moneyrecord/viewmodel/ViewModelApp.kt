package com.watering.moneyrecord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.watering.moneyrecord.entities.*
import com.watering.moneyrecord.model.AppRepository
import com.watering.moneyrecord.model.MyCalendar
import kotlinx.coroutines.*

open class ViewModelApp(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coroutineContext = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    val repository = AppRepository(application, scope)

    val allGroups = repository.allGroups
    val allAccounts = repository.allAccounts
    val allCatMains = repository.allCatMains
    val allCatSubs = repository.allCatSubs
    val allCards = repository.allCards
    val allHomes = repository.allHomes
    val allSpendCashes = repository.allSpendCashes

    val categoryOfSpend = mapOf(
        "식비" to listOf("식자재", "외식비", "간식비", "기타"),
        "교통" to listOf("대중교통비", "주유비", "차량관리비", "보험료", "자동차세", "기타"),
        "구입" to listOf("가구구입", "가전구입", "기타"),
        "생활" to listOf("관리비", "공공요금", "소모품", "수리비", "기타"),
        "통신" to listOf("유선전화", "이동통신", "인터넷", "기타"),
        "의료" to listOf("병원비", "약값", "기타"),
        "교육" to listOf("학원비", "납부금", "교재", "학용품", "기타"),
        "의류" to listOf("의류구입", "신발구입", "수선", "세탁", "기타"),
        "가족행복" to listOf("용돈지급", "선물", "여행", "기타"),
        "사회활동" to listOf("모임회비", "경조사", "기부", "기타"),
        "금융" to listOf("저축", "투자")
    )
    val categoryOfIncome = mapOf(
        "정규수입" to listOf("월급", "용돈"),
        "상여" to listOf("명절상여", "성과급", "기타"),
        "금융" to listOf("이자", "배당", "투자수익", "기타"),
        "기타수입" to listOf("기타")
    )

    companion object {
        var currentGroupId = -1
        var currentAccountId = -1
    }

    fun setCurrentAccountId(id: Int?) {
        if (id != null) currentAccountId = id
    }
    fun getCurrentGroupId(): Int {
        return currentGroupId
    }

    fun <T> insert(t: T) = scope.launch(Dispatchers.IO) { repository.insert(t) }
    fun <T> update(t: T) = scope.launch(Dispatchers.IO) { repository.update(t) }
    fun <T> delete(t: T) = scope.launch(Dispatchers.IO) { repository.delete(t) }

    fun close() = repository.close()
    fun initialize() = repository.initialize()

    fun getFirstDate(group: String?) = if(group != "") Transformations.switchMap(getGroup(group)) { repository.getFirstDate(it.id) }
            else repository.getFirstDate()
    fun getGroup(id: Int?) = repository.getGroup(id) // FragmentEditAccount, FragmentHome, Processing
    fun getGroup(name: String?) = repository.getGroup(name)  // FragmentHome, ViewModelApp

    fun getAccount(id: Int?) = repository.getAccount(id)  // FragmentAccountTransfer, FragmentEditCard, FragmentEditIncome

    fun getAccountByNumber(number: String?) = repository.getAccount(number)  // FragmentAccountTransfer, FragmentEditAccount, FragmentEditIncome, FragmentEditSpend

    fun getHomeByIdAccount(idAccount: Int?) = repository.getHomeByIdAccount(idAccount)  // FragmentEditAccount, Processing
    fun getHomesByGroup(group: String?) = repository.getHomesByGroup(group)  // FragmentHome, FragmentHomeList
    fun getHomesByGroup(id: Int?) = Transformations.switchMap(getGroup(id)) {repository.getHomesByGroup(it.name) }

    fun getCatMain(id: Int?) = repository.getCatMain(id)  // FragmentEditCategorySub, FragmentManagementCategorySub
    fun getCatMainsByKind(kind: String?) = repository.getCatMain(kind)  // FragmentEditIncome, FragmentEditSpend
    fun getCatMainBySub(idSub: Int?) = repository.getCatMainBySub(idSub)  // FragmentEditIncome, FragmentEditSpend

    fun getCatSub(id: Int?) = repository.getCatSub(id)  // FragmentEditIncome, FragmentEditSpend
    fun getCatSub(nameOfSub: String?, nameOfMain: String?) =  // FragmentEditIncome, FragmentEditSpend, FragmentManagementCategorySub
        repository.getCatSub(nameOfSub, nameOfMain)

    fun getCatSubsByMain(nameOfMain: String?) = repository.getCatSubsByMain(nameOfMain)  // ViewModelEditIncome, ViewModelEditSpend

    fun loadingIOKRW(idAccount: Int?, date: String?, isUpdateEvaluation: Boolean): LiveData<IOKRW> {
        return Transformations.switchMap(getPreviousEvaluationOfKRW(idAccount, date)) { previousEvaluation ->
            Transformations.switchMap(sumOfSpendCashForDate(idAccount, date)) { sumOfSpendsCash ->
                Transformations.switchMap(sumOfSpendCardForDate(idAccount, date)) { sumOfSpendsCard ->
                    Transformations.switchMap(sumOfIncomeForDate(idAccount, date)) { sumOfIncome ->
                        Transformations.map(getIOKRW(idAccount, date)) { io ->
                            if (io == null) {
                                val new = IOKRW()
                                new.date = date
                                new.evaluationKRW = previousEvaluation - sumOfSpendsCard - sumOfSpendsCash + sumOfIncome
                                new.account = idAccount
                                new.spendCash = sumOfSpendsCash
                                new.spendCard = sumOfSpendsCard
                                new.income = sumOfIncome
                                new.output = 0
                                new.input = 0
                                new
                            } else {
                                if(io.output == null) io.output = 0
                                if(io.input == null) io.input = 0
                                if(isUpdateEvaluation) io.evaluationKRW = previousEvaluation - sumOfSpendsCard - sumOfSpendsCash + sumOfIncome - io.output!! + io.input!!
                                io.spendCash = sumOfSpendsCash
                                io.spendCard = sumOfSpendsCard
                                io.income = sumOfIncome
                                io
                            }
                        }
                    }
                }
            }
        }
    }  // FragmentAccountTransfer, FragmentEditInoutKRW, Processing, ViewModelApp

    fun loadingIOForeign(idAccount: Int?, date: String?, currency: Int?, isUpdateEvaluation: Boolean): LiveData<IOForeign> {
        return Transformations.switchMap(getPreviousEvaluationKRWOfForeign(idAccount, date, currency)) { previousEvaluation ->
            Transformations.map(getIOForeign(idAccount, date, currency)) { io ->
                if (io == null) {
                    val new = IOForeign()
                    new.account = idAccount
                    new.currency = currency
                    new.date = date
                    new.evaluationKRW = previousEvaluation
                    new
                } else {
                    if (isUpdateEvaluation) io.evaluationKRW = previousEvaluation + io.inputKRW!! - io.outputKRW!!
                    io
                }
            }
        }
    }  // FragmentEditInoutForeign, Processing, ViewModelApp

    fun loadingDairyKRW(idAccount: Int?, date: String?, isUpdateEvaluation: Boolean): LiveData<DairyKRW> {
        return Transformations.switchMap(getDairyKRW(idAccount, date)) { dairy ->
            Transformations.switchMap(calculatePrincipalOfKRW(idAccount, date)) { principal -> Transformations.map(calculateRateOfKRW(idAccount, date, isUpdateEvaluation)) { rate ->
                if (dairy == null) {
                    val new = DairyKRW()
                    new.principalKRW = principal
                    new.rate = rate
                    new.account = idAccount
                    new.date = date
                    new
                } else {
                    dairy.principalKRW = principal
                    dairy.rate = rate
                    dairy
                } }
            }
        }
    }  // FragmentEditInoutKRW, Processing, ViewModelApp

    fun loadingDairyForeign(idAccount: Int?, date: String?, currency: Int?, isUpdateEvaluation: Boolean): LiveData<DairyForeign> {
        return Transformations.switchMap(getDairyForeign(idAccount, date, currency)) { dairy ->
            Transformations.switchMap(calculatePrincipalOfForeign(idAccount, date, currency)) { principal ->
                Transformations.switchMap(calculatePrincipalKRWOfForeign(idAccount, date, currency)) { principal_krw ->
                    Transformations.map(calculateRateOfForeign(idAccount, date, currency, isUpdateEvaluation)) { rate ->
                        if (dairy == null) {
                            val new = DairyForeign()
                            new.principal = principal
                            new.principalKRW = principal_krw
                            new.rate = rate
                            new.account = idAccount
                            new.date = date
                            new
                        } else {
                            dairy.principal = principal
                            dairy.principalKRW = principal_krw
                            dairy.rate = rate
                            dairy
                        }
                    }
                }
            }
        }
    }  // FragmentEditInoutForeign, Processing

    private fun getPreviousEvaluationOfKRW(idAccount: Int?, date: String?): LiveData<Int> {
        val before = MyCalendar.calendarToStr(MyCalendar.changeDate(date, -1))

        return Transformations.map(getLastIOKRW(idAccount, before)) { last ->
            if (last == null) 0 else last.evaluationKRW
        }
    }

    private fun getPreviousEvaluationKRWOfForeign(idAccount: Int?, date: String?, currency: Int?): LiveData<Double> {
        val before = MyCalendar.calendarToStr(MyCalendar.changeDate(date, -1))

        return Transformations.map(getLastIOForeign(idAccount, before, currency)) { last ->
            if (last == null) 0.0 else last.evaluationKRW
        }
    }

    private fun getIOKRW(idAccount: Int?, date: String?) = repository.getIOKRW(idAccount, date)
    private fun getIOForeign(idAccount: Int?, date: String?, currency: Int?) =
        repository.getIOForeign(idAccount, date, currency)

    private fun getDairyKRW(idAccount: Int?, date: String?) =
        repository.getDairyKRW(idAccount, date)

    private fun getDairyForeign(idAccount: Int?, date: String?, currency: Int?) =
        repository.getDairyForeign(idAccount, date, currency)

    private fun getLastIOKRW(idAccount: Int?, date: String?) =
        repository.getLastIOKRW(idAccount, date)

    private fun getLastIOForeign(idAccount: Int?, date: String?, currency: Int?) =
        repository.getLastIOForeign(idAccount, date, currency)

    private fun getLastDairyKRW(idAccount: Int?, date: String?) =
        repository.getLastDairyKRW(idAccount, date)

    private fun sumOfSpendCashForDate(idAccount: Int?, date: String?) =
        repository.sumOfSpendCashForDate(idAccount, date)

    private fun sumOfSpendCardForDate(idAccount: Int?, date: String?) =
        repository.sumOfSpendCardForDate(idAccount, date)

    private fun sumOfIncomeForDate(idAccount: Int?, date: String?) =
        repository.sumOfIncomeForDate(idAccount, date)

    private fun sumOfInputOfKRWUntilDate(idAccount: Int?, date: String?) =
        repository.sumOfInputOfKRWUntilDate(idAccount, date)

    private fun sumOfOutputOfKRWUntilDate(idAccount: Int?, date: String?) =
        repository.sumOfOutputOfKRWUntilDate(idAccount, date)

    private fun sumOfInputOfForeignUntilDate(idAccount: Int?, date: String?, currency: Int?) =
        repository.sumOfInputOfForeignUntilDate(idAccount, date, currency)

    private fun sumOfOutputOfForeignUntilDate(idAccount: Int?, date: String?, currency: Int?) =
        repository.sumOfOutputOfForeignUntilDate(idAccount, date, currency)

    private fun sumOfInputKRWOfForeignUntilDate(idAccount: Int?, date: String?, currency: Int?) =
        repository.sumOfInputKRWOfForeignUntilDate(idAccount, date, currency)

    private fun sumOfOutputKRWOfForeignUntilDate(idAccount: Int?, date: String?, currency: Int?) =
        repository.sumOfOutputKRWOfForeignUntilDate(idAccount, date, currency)

    private fun sumOfIncomeUntilDate(idAccount: Int?, date: String?) =
        repository.sumOfIncomeUntilDate(idAccount, date)

    private fun sumOfSpendCardUntilDate(idAccount: Int?, date: String?) =
        repository.sumOfSpendCardUntilDate(idAccount, date)

    private fun sumOfSpendCashUntilDate(idAccount: Int?, date: String?) =
        repository.sumOfSpendCashUntilDate(idAccount, date)

    // 지정일까지 원화 원금을 원화 입금액, 출금액, 지출, 수입 전체로 계산
    private fun calculatePrincipalOfKRW(idAccount: Int?, date: String?): LiveData<Int> {
       return Transformations.switchMap(sumOfInputOfKRWUntilDate(idAccount, date)) { sum_input ->
           Transformations.switchMap(sumOfIncomeUntilDate(idAccount, date)) { sum_income ->
               Transformations.switchMap(sumOfOutputOfKRWUntilDate(idAccount, date)) { sum_output ->
                   Transformations.switchMap(sumOfSpendCardUntilDate(idAccount, date)) { sum_card ->
                       Transformations.map(sumOfSpendCashUntilDate(idAccount, date)) { sum_cash ->
                           var input = 0
                           var output = 0

                           sum_input?.let { input += it }
                           sum_income?.let { input += it }
                           sum_output?.let { output += it }
                           sum_card?.let { output += it }
                           sum_cash?.let { output += it }

                           input - output
                       }
                   }
               }
           }
       }
    }
    // 지정일까지 외화 원금을 외화 입금액, 출금액 전체로 계산
    private fun calculatePrincipalOfForeign(idAccount: Int?, date: String?, currency: Int?): LiveData<Double> {
        return Transformations.switchMap(sumOfInputOfForeignUntilDate(idAccount, date, currency)) { sum_input ->
            Transformations.map(sumOfOutputOfForeignUntilDate(idAccount, date, currency)) { sum_output ->
                var input = 0.0
                var output = 0.0
                sum_input?.let { input = it }
                sum_output?.let { output = it }

                input - output
            }
        }
    }
    // 지정일까지 외화의 입금, 출금을 원화 기준으로 계산
    private fun calculatePrincipalKRWOfForeign(idAccount: Int?, date: String?, currency: Int?): LiveData<Int> {
        return Transformations.switchMap(sumOfInputKRWOfForeignUntilDate(idAccount, date, currency)) { sum_input_krw ->
            Transformations.map(sumOfOutputKRWOfForeignUntilDate(idAccount, date, currency)) { sum_output_krw ->
                var input = 0
                var output = 0

                sum_input_krw?.let { input = it }
                sum_output_krw?.let { output = it }

                input - output
            }
        }
    }

    private fun calculateRateOfKRW(idAccount: Int?, date: String?, isUpdateEvaluation: Boolean): LiveData<Double> {
        return Transformations.switchMap(loadingIOKRW(idAccount, date, isUpdateEvaluation)) { io ->
            Transformations.map(calculatePrincipalOfKRW(idAccount, date)) { principal ->
                io.evaluationKRW!!.toDouble() / principal * 100 - 100
            }
        }
    }
    private fun calculateRateOfForeign(idAccount: Int?, date: String?, currency: Int?, isUpdateEvaluation: Boolean): LiveData<Double> {
        return Transformations.switchMap(loadingIOForeign(idAccount, date, currency, isUpdateEvaluation)) { io ->
            Transformations.map(calculatePrincipalKRWOfForeign(idAccount, date, currency)) { principalKRW ->
                io.evaluationKRW!! / principalKRW * 100 - 100
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}