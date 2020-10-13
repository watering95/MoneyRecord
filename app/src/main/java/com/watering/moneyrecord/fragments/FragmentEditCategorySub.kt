package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditCategorysubBinding
import com.watering.moneyrecord.entities.CategorySub
import com.watering.moneyrecord.viewmodel.ViewModelEditCategorySub
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class FragmentEditCategorySub : ParentFragment() {
    private lateinit var item: CategorySub
    private lateinit var binding:FragmentEditCategorysubBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_edit_categorysub, container, false)
        initLayout()
        return binding.root
    }
    fun initInstance(item: CategorySub):FragmentEditCategorySub {
        this.item = item
        return this
    }
    private fun initLayout() {
        setHasOptionsMenu(true)

        mViewModel.allCatMains.observeOnce { list ->
            list?.let {
                list.map { it.name }.let { list ->
                    when {
                        this.item.id != null -> mViewModel.getCatMain(this.item.categoryMain)
                            .observeOnce { main ->
                                main?.let {
                                    binding.viewmodel =
                                        ViewModelEditCategorySub(this.item, list.indexOf(main.name))
                                    binding.adapter = ArrayAdapter(
                                        activity,
                                        android.R.layout.simple_spinner_item,
                                        list
                                    )

                                }
                            }
                        else -> {
                            binding.viewmodel = ViewModelEditCategorySub(this.item, 0)
                            binding.adapter =
                                ArrayAdapter(activity, android.R.layout.simple_spinner_item, list)
                        }
                    }
                }
            }
        }
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

                        categorySub?.run {
                            if(name.isNullOrEmpty() || selected < 0) {
                                Toast.makeText(activity, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                            } else {
                                mViewModel.allCatMains.observeOnce { list ->
                                    list?.let {
                                        apply {
                                            selected.let {
                                                categoryMain = list[it].id
                                            }
                                        }.let { sub ->
                                            val job = when (this@FragmentEditCategorySub.item.id) {
                                                null -> mViewModel.insert(sub)
                                                else -> mViewModel.update(sub)
                                            }
                                            runBlocking {
                                                job.join()
                                                Toast.makeText(
                                                    activity,
                                                    R.string.toast_save_success,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                mFragmentManager.popBackStack()
                                            }
                                        }
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

                    val job = mViewModel.delete(this@FragmentEditCategorySub.item)
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