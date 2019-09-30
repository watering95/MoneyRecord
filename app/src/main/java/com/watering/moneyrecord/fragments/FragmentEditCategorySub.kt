package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditCategorysubBinding
import com.watering.moneyrecord.entities.CategorySub
import com.watering.moneyrecord.viewmodel.ViewModelApp
import com.watering.moneyrecord.viewmodel.ViewModelEditCategorySub
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class FragmentEditCategorySub : Fragment() {
    private lateinit var item: CategorySub
    private lateinit var mViewModel: ViewModelApp
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
        val activity = activity as MainActivity
        mViewModel = activity.mViewModel

        setHasOptionsMenu(true)

        mViewModel.allCatMains.observeOnce( Observer { list -> list?.let {
            list.map { it.name }.let { list ->
                when {
                    this.item.id != null -> mViewModel.getCatMain(this.item.categoryMain).observeOnce( Observer { main -> main?.let {
                        binding.viewmodel = ViewModelEditCategorySub(this.item, list.indexOf(main.name))
                        binding.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list)

                    } })
                    else -> {
                        binding.viewmodel = ViewModelEditCategorySub(this.item, 0)
                        binding.adapter = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list)
                    }
                }
            }
        } })
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
                                mViewModel.allCatMains.observeOnce( Observer { list -> list?.let {
                                    apply { selected.let { categoryMain = list[it].id } }.let { sub ->
                                        val job = when(this@FragmentEditCategorySub.item.id) {
                                            null -> mViewModel.insert(sub)
                                            else -> mViewModel.update(sub)
                                        }
                                        runBlocking {
                                            job.cancelAndJoin()
                                            delay(100)
                                            Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                            fragmentManager?.popBackStack()
                                        }
                                    }
                                } })
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
                        job.cancelAndJoin()
                        delay(100)
                        Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                        fragmentManager?.popBackStack()
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object: Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}