package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditCardBinding
import com.watering.moneyrecord.entities.Card
import com.watering.moneyrecord.viewmodel.ViewModelEditCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class FragmentEditCard : ParentFragment() {
    private lateinit var item: Card
    private lateinit var binding: FragmentEditCardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_edit_card, container, false)
        initLayout()
        return binding.root
    }
    fun initInstance(item: Card):FragmentEditCard {
        this.item = item
        return this
    }
    private fun initLayout() {
        setHasOptionsMenu(true)

        mViewModel.allAccounts.observeOnce { list ->
            list?.let {
                list.map { it.number }.let { list ->
                    when {
                        this.item.id != null -> mViewModel.getAccount(this.item.account)
                            .observeOnce { account ->
                                account?.let {
                                    binding.viewmodel =
                                        ViewModelEditCard(this.item, list.indexOf(account.number))
                                    binding.adapter = ArrayAdapter(
                                        activity,
                                        android.R.layout.simple_spinner_item,
                                        list
                                    )
                                }
                            }
                        else -> {
                            binding.viewmodel = ViewModelEditCard(this.item, 0)
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

                        card?.run {
                            if(selected < 0 || name.isNullOrEmpty() || number.isNullOrEmpty() || company.isNullOrEmpty() || drawDate.isNullOrEmpty())
                                Toast.makeText(activity, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                            else {
                                mViewModel.allAccounts.observeOnce { list ->
                                    list?.let {
                                        card?.apply { selected.let { account = list[it].id } }
                                            .let { card ->
                                                val job = when (this@FragmentEditCard.item.id) {
                                                    null -> mViewModel.insert(card)
                                                    else -> mViewModel.update(card)
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

                    val job = mViewModel.delete(this@FragmentEditCard.item)
                    runBlocking {
                        job.join()
                        Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                        mFragmentManager.popBackStack()
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}