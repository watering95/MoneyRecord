package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditCategorymainBinding
import com.watering.moneyrecord.entities.CategoryMain
import com.watering.moneyrecord.viewmodel.ViewModelEditCategoryMain
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class FragmentEditCategoryMain : ParentFragment() {
    private lateinit var item: CategoryMain
    private lateinit var binding:FragmentEditCategorymainBinding
    private lateinit var mList:List<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_edit_categorymain, container, false)
        initLayout()
        return binding.root
    }
    fun initInstance(item: CategoryMain):FragmentEditCategoryMain {
        this.item = item
        return this
    }
    private fun initLayout() {
        mList = resources.getStringArray(R.array.category).toList()

        binding.viewmodel = ViewModelEditCategoryMain(this.item, mList.indexOf(this.item.kind))

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_edit,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_edit_save -> {
                binding.viewmodel?.run {
                    runBlocking {
                        delay(100)

                        categoryMain?.run {
                            if(name.isNullOrEmpty()) Toast.makeText(activity, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                            else {
                                categoryMain?.apply { selected?.let { kind = mList[it] } }.let { main ->
                                    val job = when(this@FragmentEditCategoryMain.item.id) {
                                        null -> mViewModel.insert(main)
                                        else -> mViewModel.update(main)
                                    }
                                    runBlocking {
                                        job.join()
                                        Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                        mFragmentManager.popBackStack()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            R.id.menu_edit_delete -> {
                runBlocking {
                    delay(100)

                    val job = mViewModel.delete(this@FragmentEditCategoryMain.item)
                    runBlocking {
                        job.join()
                        Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                        mFragmentManager.popBackStack()
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}