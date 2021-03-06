package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil.inflate
import androidx.recyclerview.widget.LinearLayoutManager
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentManagementDbBinding
import com.watering.moneyrecord.view.RecyclerViewAdapterFileManagementDB
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementDB
import com.watering.moneyrecord.viewmodel.ViewModelManagementDB

class FragmentManagementDB : ParentFragment() {
    private lateinit var binding: FragmentManagementDbBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_management_db, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = activity.application?.let { ViewModelManagementDB(it) }
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_management_db)

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
                mViewModel.close()
                activity.mDBFile.requestUpload()
            }
            // 파일 복원
            1 -> {
                mViewModel.close()
                activity.mDBFile.requestDownload()
                mViewModel.initialize()
                getList()
            }
            // 파일 삭제
            2 -> {
                mViewModel.close()
                activity.mDBFile.requestDelete()
                mViewModel.initialize()
                getList()
            }
        }
    }

    private fun getList() {
        binding.viewmodel?.run {
            listOfFile = activity.getDatabasePath(activity.mDBFile.dbFileName).parentFile.list().filterNotNull()
            binding.recyclerviewFileFragmentManagementDb.run {
                adapter = RecyclerViewAdapterFileManagementDB(listOfFile)
            }
        }
    }
}