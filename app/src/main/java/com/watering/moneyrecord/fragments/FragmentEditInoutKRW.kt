package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import com.watering.moneyrecord.BR
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditInoutKrwBinding
import com.watering.moneyrecord.model.Converter
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.viewmodel.ViewModelEditInoutKRW
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class FragmentEditInoutKRW : ParentFragment() {
    private lateinit var binding: FragmentEditInoutKrwBinding
    private var idAccount:Int? = 0
    private var date:String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_edit_inout_krw, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = application?.let { ViewModelEditInoutKRW(it) }

        initLayout()

        return binding.root
    }

    fun initInstance(id_account:Int?, date:String?):FragmentEditInoutKRW {
        this.idAccount = id_account
        this.date = date
        return this
    }

    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_inout_krw)

        binding.viewmodel?.run {
            addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    when(propertyId) {
                        BR.date -> onChangedDate()
                    }
                }
            })

            date = this@FragmentEditInoutKRW.date
        }

        binding.buttonDateFragmentEditInoutKrw.setOnClickListener {
            binding.viewmodel?.run {
                val dialog = DialogDate().newInstance(date, object:DialogDate.Complete {
                    override fun onComplete(date: String?) {
                        val select = MyCalendar.strToCalendar(date)
                        when {
                            Calendar.getInstance().before(select) -> Toast.makeText(activity, R.string.toast_date_error, Toast.LENGTH_SHORT).show()
                            else -> {
                                this@run.date = MyCalendar.calendarToStr(select)
                            }
                        }
                    }
                })
                mFragmentManager.let { dialog.show(it, "dialog") }
            }
        }
        binding.buttonUpdateEvaluationFragmentEditInoutKrw.setOnClickListener {
            binding.viewmodel?.run {
                evaluation = evaluation!! - withdraw!! + deposit!!
            }
        }

        Converter.addConvertedTextChangedListener(binding.editWithdrawFragmentEditInoutKrw)
        Converter.addConvertedTextChangedListener(binding.editDepositFragmentEditInoutKrw)
        Converter.addConvertedTextChangedListener(binding.editEvaluationFragmentEditInoutKrw)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_edit,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_edit_save -> save()
            R.id.menu_edit_delete -> binding.viewmodel?.run {
                runBlocking {
                    delay(100)

                    runBlocking {
                        delete(io).join()
                        Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                        processing?.ioKRW(idAccount, io.date)
                    }
                }

            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        binding.viewmodel?.run {
            runBlocking {
                delay(100)

                val jobIO = if(io.id == null) insert(io) else update(io)

                runBlocking {
                    jobIO.join()
                    Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                    processing?.ioKRW(idAccount, io.date)
                }
            }

        }
    }

    private fun onChangedDate() {
        binding.viewmodel?.run {
            loadingIOKRW(idAccount, date, false).observeOnce { io ->
                io?.let {
                    income = io.income
                    evaluation = io.evaluationKRW
                    deposit = io.input
                    spend = io.spendCard!! + io.spendCash!!
                    withdraw = io.output
                    this.io = io
                    loadingDairyKRW(
                        idAccount,
                        date,
                        false
                    ).observeOnce { dairy -> dairy?.let { principal = dairy.principalKRW } }
                }
            }
        }
    }
}