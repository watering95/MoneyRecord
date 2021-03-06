package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditAccountBinding
import com.watering.moneyrecord.entities.Account
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.viewmodel.ViewModelEditAccount
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class FragmentEditAccount : ParentFragment() {
    private lateinit var item: Account
    private lateinit var binding:FragmentEditAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_edit_account, container, false)
        initLayout()
        return binding.root
    }
    fun initInstance(item: Account):FragmentEditAccount {
        this.item = item
        return this
    }
    private fun initLayout() {
        setHasOptionsMenu(true)

        mViewModel.allGroups.observe(viewLifecycleOwner, { list -> list?.let {
            list.map { it.name }.let { listOfAdapter ->
                when {
                    this.item.id != null -> mViewModel.getGroup(this.item.group).observe(viewLifecycleOwner,
                        { group -> group?.let {
                            binding.viewmodel = ViewModelEditAccount(this.item, listOfAdapter.indexOf(group.name))
                        } })
                    else -> binding.viewmodel = ViewModelEditAccount(this.item, 0)
                }
                binding.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item,listOfAdapter)
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
            R.id.menu_edit_save -> save()
            R.id.menu_edit_delete -> {
                mViewModel.run {
                    runBlocking {
                        delay(100)
                        delete(this@FragmentEditAccount.item)
                        getHomeByIdAccount(this@FragmentEditAccount.item.id).observeOnce { home -> home?.let {
                            val job = delete(it)
                            runBlocking {
                                job.join()
                                Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                                mFragmentManager.popBackStack()
                            }
                        } }
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        binding.viewmodel?.run {
            runBlocking {
                delay(100)

                account?.run {
                    if(description.isNullOrEmpty() || institute.isNullOrEmpty() || number.isNullOrEmpty())
                        Toast.makeText(activity, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                    else {
                        mViewModel.run {
                            allGroups.observeOnce { list -> list?.let {
                                account?.apply { selected?.let { group = list[it].id } }.let { account ->
                                    when (this@FragmentEditAccount.item.id) {
                                        null -> {
                                            val job = insert(account)
                                            runBlocking {
                                                job.join()
                                                delay(100)
                                                getAccountByNumber(this@FragmentEditAccount.item.number).observeOnce { account -> account?.let {
                                                    getGroup(account.group).observeOnce { group -> group?.let {
                                                        val home = Home()
                                                        home.idAccount = account.id
                                                        home.account = account.number
                                                        home.description = account.institute + account.description
                                                        home.group = group.name
                                                        val jj = insert(home)
                                                        runBlocking {
                                                            jj.join()
                                                            Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                                            mFragmentManager.popBackStack()
                                                        }
                                                    } }
                                                } }
                                            }
                                        }
                                        else -> {
                                            val job = update(account)
                                            runBlocking {
                                                job.join()
                                                delay(100)
                                                Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                                getHomeByIdAccount(account?.id).observeOnce { home -> home?.let {
                                                    getGroup(account?.group).observeOnce { group -> group?.let {
                                                        home.account = account?.number
                                                        home.description = account?.institute + " " + account?.description
                                                        home.group = group.name
                                                        val jj = update(home)
                                                        runBlocking {
                                                            jj.join()
                                                            Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                                            mFragmentManager.popBackStack()
                                                        }
                                                    } }
                                                } }
                                            }
                                        }
                                    }
                                }
                            } }
                        }
                    }
                }
            }
        }
    }
}