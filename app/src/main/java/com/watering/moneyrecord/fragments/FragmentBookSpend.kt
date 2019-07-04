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
import com.watering.moneyrecord.databinding.FragmentBookSpendBinding
import com.watering.moneyrecord.entities.Spend
import com.watering.moneyrecord.model.ModelCalendar
import com.watering.moneyrecord.view.RecyclerViewAdapterBookSpend
import com.watering.moneyrecord.viewmodel.ViewModelApp
import java.util.*

class FragmentBookSpend : Fragment() {
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding: FragmentBookSpendBinding
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager as FragmentManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_book_spend, container, false)
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        val activity = activity as MainActivity

        mViewModel = activity.mViewModel
        binding.date = ModelCalendar.getToday()

        setHasOptionsMenu(false)

        binding.buttonCalendarFragmentBookSpend.setOnClickListener {
            val dialog = DialogDate().newInstance(binding.date, object:DialogDate.Complete {
                override fun onComplete(date: String?) {
                    val select = ModelCalendar.strToCalendar(date)
                    when {
                        Calendar.getInstance().before(select) -> Toast.makeText(activity, R.string.toast_date_error, Toast.LENGTH_SHORT).show()
                        else -> binding.date = ModelCalendar.calendarToStr(select)
                    }
                }
            })
            dialog.show(fragmentManager, "dialog")
        }
        binding.buttonBackwardFragmentBookSpend.setOnClickListener {
            val date = ModelCalendar.changeDate(binding.date.toString(), -1)
            binding.date = ModelCalendar.calendarToStr(date)
        }
        binding.buttonForwardFragmentBookSpend.setOnClickListener {
            val date = ModelCalendar.changeDate(binding.date.toString(), 1)
            when {
                Calendar.getInstance().before(date) -> Toast.makeText(activity, R.string.toast_date_error, Toast.LENGTH_SHORT).show()
                else -> binding.date = ModelCalendar.calendarToStr(date)
            }
        }

        binding.textDateFragmentBookSpend.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mViewModel.getSpends(binding.date).observe(this@FragmentBookSpend, Observer { list -> list?.let {
                    binding.recyclerviewFragmentBookSpend.run {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = RecyclerViewAdapterBookSpend(list) { position -> itemClicked(list[position]) }
                    }
                } })
            }
        })

        binding.floatingFragmentBookSpend.setOnClickListener {
            val spend = Spend().apply { date = binding.date }
            mViewModel.replaceFragment(mFragmentManager, FragmentEditSpend().initInstance(spend))
        }
    }
    private fun itemClicked(item: Spend) {
        mViewModel.replaceFragment(mFragmentManager, FragmentEditSpend().initInstance(item))
    }
}