package com.watering.moneyrecord

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.watering.moneyrecord.fragments.*
import com.watering.moneyrecord.model.DBFile
import com.google.android.material.navigation.NavigationBarView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mFragmentManager = this.supportFragmentManager
    private var mTransaction = mFragmentManager.beginTransaction()
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        res -> this.onSignInResult(res)
    }
    val mDBFile = DBFile(this)
    var mUser : FirebaseUser? = null

    private val mapOfFragments = mutableMapOf<Int, Fragments>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapOfFragments[R.id.navigation_home] = Fragments(getString(R.string.app_name),FragmentHome())
        mapOfFragments[R.id.navigation_book] = Fragments(getString(R.string.title_book),FragmentBook())
        mapOfFragments[R.id.navigation_accounts] = Fragments(getString(R.string.title_accounts),FragmentAccounts())
        mapOfFragments[R.id.navigation_management] = Fragments(getString(R.string.title_management),FragmentManagement())

        mapOfFragments[R.id.navigation_home]?.fragment?.let { mTransaction.add(R.id.frame_main, it).commit() }

        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener)

        signIn()
    }

    private val mOnNavigationItemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
        return@OnItemSelectedListener replaceFragment(mapOfFragments[item.itemId])
    }

    private fun replaceFragment(fragment: Fragments?): Boolean {
        mTransaction = mFragmentManager.beginTransaction()
        fragment?.let {
            supportActionBar?.title = it.title
            with(mTransaction) {
                replace(R.id.frame_main, it.fragment)
                commit()
                return true
            }
        }

        return false
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signinIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
        signInLauncher.launch(signinIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if(result.resultCode == Activity.RESULT_OK) {
            mUser = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "${mUser?.displayName} ${getString(R.string.toast_login_success)}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "$response ${getString(R.string.toast_login_error)}", Toast.LENGTH_SHORT).show()
        }
    }
}
