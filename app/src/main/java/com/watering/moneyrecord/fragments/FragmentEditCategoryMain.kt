package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditCategorymainBinding
import com.watering.moneyrecord.entities.CategoryMain
import com.watering.moneyrecord.viewmodel.ViewModelApp
import com.watering.moneyrecord.viewmodel.ViewModelEditCategoryMain

class FragmentEditCategoryMain : Fragment() {
    private lateinit var item: CategoryMain
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding:FragmentEditCategorymainBinding
    private lateinit var mList:List<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_edit_categorymain, container, false)
        initLayout()
        return binding.root
    }
    fun initInstance(item: CategoryMain):FragmentEditCategoryMain {
        this.item = item
        return this
    }
    private fun initLayout() {
        val activity = activity as MainActivity
        mViewModel = activity.mViewModel

        mList = resources.getStringArray(R.array.category).toList()

        binding.viewmodel = ViewModelEditCategoryMain(this.item, mList.indexOf(this.item.kind))

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_edit,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_edit_save -> {
                binding.viewmodel?.run {
                    categoryMain?.run {
                        if(name.isNullOrEmpty()) Toast.makeText(activity, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                        else {
                            categoryMain?.apply { selected?.let { kind = mList[it] } }.let { main ->
                                when {
                                    this@FragmentEditCategoryMain.item.id == null -> mViewModel.insert(main)
                                    else -> mViewModel.update(main)
                                }
                                fragmentManager?.popBackStack()
                            }
                        }
                    }
                }
            }
            R.id.menu_edit_delete -> {
                mViewModel.delete(this.item)
                fragmentManager?.popBackStack()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}