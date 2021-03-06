package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditGroupBinding
import com.watering.moneyrecord.entities.Group
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class FragmentEditGroup : ParentFragment() {
    private lateinit var item: Group
    private lateinit var binding:FragmentEditGroupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_edit_group, container, false)
        initLayout()
        return binding.root
    }
    fun initInstance(item: Group):FragmentEditGroup {
        this.item = item
        return this
    }
    private fun initLayout() {
        setHasOptionsMenu(true)
        binding.group = this.item
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_edit,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_edit_save -> {
                runBlocking {
                    delay(100)

                    if(binding.group!!.name.isNullOrEmpty()) {
                        Toast.makeText(activity, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                    } else {
                        val job = when(this@FragmentEditGroup.item.id) {
                            null -> mViewModel.insert(binding.group)
                            else -> mViewModel.update(binding.group)
                        }
                        runBlocking {
                            job.join()
                            Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                            mFragmentManager.popBackStack()
                        }
                    }
                }
            }
            R.id.menu_edit_delete -> {
                runBlocking {
                    delay(100)

                    val job = mViewModel.delete(this@FragmentEditGroup.item)
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