package com.watering.moneyrecord

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.watering.moneyrecord.fragments.FragmentHome
import com.watering.moneyrecord.viewmodel.ViewModelApp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mFragmentManager = this.supportFragmentManager!!
    private var mTransaction = mFragmentManager.beginTransaction()
    private val mFragmentHome = FragmentHome()
    lateinit var mViewModel: ViewModelApp

    val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTransaction.add(R.id.frame_main, mFragmentHome).commit()

        mViewModel = ViewModelProviders.of(this).get(ViewModelApp::class.java)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        signIn()
    }

    fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        mTransaction = mFragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_home -> {
                supportActionBar?.title = getString(R.string.app_name)
                with(mTransaction) {
                    replace(R.id.frame_main, mFragmentHome)
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_book -> {
                supportActionBar?.title = getString(R.string.title_book)
                with(mTransaction) {
//                    replace(R.id.frame_main, mFragmentBook)
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_accounts -> {
                supportActionBar?.title = getString(R.string.title_accounts)
                with(mTransaction) {
//                    replace(R.id.frame_main, mFragmentAccounts)
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_management -> {
                supportActionBar?.title = getString(R.string.title_management)
                with(mTransaction) {
//                    replace(R.id.frame_main, mFragmentManagement)
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {

            }
        }
    }
}
