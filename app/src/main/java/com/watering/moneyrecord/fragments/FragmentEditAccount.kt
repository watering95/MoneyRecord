package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditAccountBinding
import com.watering.moneyrecord.entities.Account
import com.watering.moneyrecord.viewmodel.ViewModelApp
import com.watering.moneyrecord.viewmodel.ViewModelEditAccount

class FragmentEditAccount : Fragment() {
    private lateinit var item: Account
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding:FragmentEditAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_edit_account, container, false)
        initLayout()
        return binding.root
    }
    fun initInstance(item: Account):FragmentEditAccount {
        this.item = item
        return this
    }
    private fun initLayout() {
        val activity = activity as MainActivity
        mViewModel = activity.mViewModel

        setHasOptionsMenu(true)

        mViewModel.allGroups.observe(this, Observer { list -> list?.let {
            list.map { it.name }.let { listOfAdapter ->
                when {
                    this.item.id != null -> mViewModel.getGroup(this.item.group).observe(this, Observer { group -> group?.let {
                        binding.viewmodel = ViewModelEditAccount(this.item, listOfAdapter.indexOf(group.name))
                    } })
                    else -> binding.viewmodel = ViewModelEditAccount(this.item, 0)
                }
                binding.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,listOfAdapter)
            }
        } })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_edit,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_edit_save -> {
                mViewModel.allGroups.observe(this, Observer { list -> list?.let {
                    binding.viewmodel?.run {
                        account?.apply { selected?.let { group = list[it].id } }.let {account ->
                            when {
                                this@FragmentEditAccount.item.id == null -> mViewModel.insert(account)
                                else -> mViewModel.update(account)
                            }
                        }
                    }}
                })
            }
            R.id.menu_edit_delete -> { mViewModel.delete(this.item) }
        }
        fragmentManager?.popBackStack()

        return super.onOptionsItemSelected(item)
    }
}