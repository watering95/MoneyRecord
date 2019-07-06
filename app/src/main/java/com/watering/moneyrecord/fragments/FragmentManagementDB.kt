package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentManagementDbBinding
import com.watering.moneyrecord.view.RecyclerViewAdapterFileManagementDB
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementDB
import com.watering.moneyrecord.viewmodel.ViewModelManagementDB

class FragmentManagementDB : Fragment() {
    private lateinit var binding: FragmentManagementDbBinding
    private lateinit var mMainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_management_db, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = ViewModelProviders.of(this).get(ViewModelManagementDB::class.java)
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        mMainActivity = activity as MainActivity
        mMainActivity.supportActionBar?.setTitle(R.string.title_management_db)

        setHasOptionsMenu(false)

        binding.viewmodel?.run {
            binding.recyclerviewFragmentManagementDb.run {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@FragmentManagementDB.context)
                adapter = RecyclerViewAdapterManagementDB(arrayListOf("파일백업","파일복원", "파일삭제")) { position -> itemClicked(position) }
            }
            binding.recyclerviewFileFragmentManagementDb.run {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@FragmentManagementDB.context)
            }

            getList()
        }
    }

    private fun itemClicked(position: Int) {
        when(position) {
            // 파일 백업
            0 -> {
                mMainActivity.mViewModel.close()
//                mMainActivity.mAppDBFile.requestBackup()
            }
            // 파일 복원
            1 -> {
//                mMainActivity.mAppDBFile.requestOpen()
                getList()
            }
            // 파일 삭제
            2 -> {
//                mMainActivity.mAppDBFile.requestDelete()
                getList()
            }
        }
    }

    private fun getList() {
        binding.viewmodel?.run {
//            listOfFile = mMainActivity.getDatabasePath(mMainActivity.mAppDBFile.dbFileName).parentFile.list().filterNotNull()
//            binding.recyclerviewFileFragmentManagementDb.run {
//                adapter = RecyclerViewAdapterFileManagementDB(listOfFile)
//            }
        }
    }
}