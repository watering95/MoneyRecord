package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditGroupBinding
import com.watering.moneyrecord.entities.Group
import com.watering.moneyrecord.viewmodel.ViewModelApp

class FragmentEditGroup : Fragment() {
    private lateinit var item: Group
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding:FragmentEditGroupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_edit_group, container, false)
        initLayout()
        return binding.root
    }
    fun initInstance(item: Group):FragmentEditGroup {
        this.item = item
        return this
    }
    private fun initLayout() {
        val activity = activity as MainActivity
        mViewModel = activity.mViewModel

        setHasOptionsMenu(true)
        binding.group = this.item
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_edit,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_edit_save -> {
                if(this.item.id == null) mViewModel.insert(binding.group)
                else mViewModel.update(binding.group)
            }
            R.id.menu_edit_delete -> { mViewModel.delete(this.item) }
        }

        fragmentManager?.popBackStack()

        return super.onOptionsItemSelected(item)
    }
}