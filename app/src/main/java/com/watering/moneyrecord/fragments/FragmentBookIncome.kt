package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentBookIncomeBinding
import com.watering.moneyrecord.entities.Income
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.view.RecyclerViewAdapterBookIncome
import com.watering.moneyrecord.viewmodel.ViewModelApp
import java.util.*

class FragmentBookIncome : Fragment() {
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding: FragmentBookIncomeBinding
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager as FragmentManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_book_income, container, false)
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        val activity = activity as MainActivity

        mViewModel = activity.mViewModel
        binding.date = MyCalendar.getToday()

        setHasOptionsMenu(false)

        binding.buttonCalendarFragmentBookIncome.setOnClickListener {
            val dialog = DialogDate().newInstance(binding.date, object: DialogDate.Complete {
                override fun onComplete(date: String?) {
                    val select = MyCalendar.strToCalendar(date)
                    when {
                        Calendar.getInstance().before(select) -> Toast.makeText(activity, R.string.toast_date_error, Toast.LENGTH_SHORT).show()
                        else -> binding.date = MyCalendar.calendarToStr(select)
                    }
                }
            })
            fragmentManager?.let { it -> dialog.show(it, "dialog") }
        }

        binding.buttonBackwardFragmentBookIncome.setOnClickListener {
            val date = MyCalendar.changeDate(binding.date.toString(), -1)
            binding.date = MyCalendar.calendarToStr(date)
        }
        binding.buttonForwardFragmentBookIncome.setOnClickListener {
            val date = MyCalendar.changeDate(binding.date.toString(), 1)
            when {
                Calendar.getInstance().before(date) -> Toast.makeText(
                    activity,
                    R.string.toast_date_error,
                    Toast.LENGTH_SHORT
                ).show()
                else -> binding.date = MyCalendar.calendarToStr(date)
            }
        }

        binding.textDateFragmentBookIncome.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mViewModel.getIncomes(binding.date).observe(this@FragmentBookIncome, Observer { list -> list?.let {
                    binding.recyclerviewFragmentBookIncome.run {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = RecyclerViewAdapterBookIncome(list) { position -> itemClicked(list[position]) }
                    }
                } })
            }
        })

        binding.floatingFragmentBookIncome.setOnClickListener {
            val income = Income().apply { date = binding.date }
            mViewModel.replaceFragment(mFragmentManager, FragmentEditIncome().initInstance(income))
        }
    }
    private fun itemClicked(item: Income) {
        mViewModel.replaceFragment(mFragmentManager, FragmentEditIncome().initInstance(item))
    }
}