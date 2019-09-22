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
import com.watering.moneyrecord.databinding.FragmentEditAccountBinding
import com.watering.moneyrecord.entities.Account
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.viewmodel.ViewModelApp
import com.watering.moneyrecord.viewmodel.ViewModelEditAccount
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_edit,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_edit_save -> {
                binding.viewmodel?.run {
                    account?.run {
                        if(description.isNullOrEmpty() || institute.isNullOrEmpty() || number.isNullOrEmpty())
                            Toast.makeText(activity, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                        else save()
                    }
                }
            }
            R.id.menu_edit_delete -> {
                mViewModel.run {
                    delete(this@FragmentEditAccount.item)
                    getHomeByIdAccount(this@FragmentEditAccount.item.id).observeOnce(Observer { home -> home?.let {
                        val job = delete(it)
                        runBlocking {
                            job.cancelAndJoin()
                            Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                            fragmentManager?.popBackStack()
                        }
                    } })
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        mViewModel.allGroups.observeOnce( Observer { list -> list?.let {
            binding.viewmodel?.run {
                account?.apply { selected?.let { group = list[it].id } }.let { account ->
                    when {
                        this@FragmentEditAccount.item.id == null -> {
                            mViewModel.run {
                                val job = insert(account)
                                runBlocking {
                                    job.cancelAndJoin()
                                    getAccountByNumber(this@FragmentEditAccount.item.number).observeOnce( Observer { account -> account?.let {
                                        getGroup(account.group).observeOnce( Observer { group -> group?.let {
                                            val home = Home()
                                            home.idAccount = account.id
                                            home.account = account.number
                                            home.description = account.institute + account.description
                                            home.group = group.name
                                            val jj = insert(home)
                                            runBlocking {
                                                jj.cancelAndJoin()
                                                Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                                fragmentManager?.popBackStack()
                                            }
                                        }})
                                    } })
                                }
                            }
                        }
                        else -> {
                            mViewModel.run {
                                val job = update(account)
                                runBlocking {
                                    job.cancelAndJoin()
                                    Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                    getHomeByIdAccount(account?.id).observeOnce( Observer { home -> home?.let {
                                        getGroup(account?.group).observeOnce( Observer { group -> group?.let {
                                            home.account = account?.number
                                            home.description = account?.institute + " " + account?.description
                                            home.group = group.name
                                            val jj = update(home)
                                            runBlocking {
                                                jj.cancelAndJoin()
                                                Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                                fragmentManager?.popBackStack()
                                            }
                                        } })
                                    } })
                                }
                            }
                        }
                    }
                }
            }}
        })
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