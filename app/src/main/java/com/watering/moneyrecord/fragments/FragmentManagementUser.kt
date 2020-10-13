package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.User
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementUser

class FragmentManagementUser : ParentFragment() {
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_management_user, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_management_user)

        val users = mutableListOf<User>()
        val user = User()

        user.name = activity.mUser?.displayName
        user.id = activity.mUser?.uid
        users.add(user)

        mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_user).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(mView.context)
            adapter = RecyclerViewAdapterManagementUser(users) { position -> itemClicked(users[position]) }
        }

        setHasOptionsMenu(false)
    }
    private fun itemClicked(item: User) {
    }
}