package com.example.whatsappclone

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.whatsappclone.ui.fragments.ChatFragment
import com.example.whatsappclone.ui.fragments.SearchFragment
import com.example.whatsappclone.ui.fragments.SettingFragment
import com.example.whatsappclone.ui.viewModel.HomeViewModel
import com.example.whatsappclone.ui.viewModel.HomeViewModelFactory
import com.example.whatsappclone.util.startLoginActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {
    var refUser: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        val tableLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(
            tableLayout,
            viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = "Chat"
                    1 -> tab.text = "Search"
                    2 -> tab.text = "Profile"
                }
            }).attach()

        viewModel.retrieveUserInfo().observe(this, Observer {
            Log.d("user", it.toString())
            username.text = it!!.username
            if (it.profile == "") {
                Picasso.get().load(R.drawable.ic_profile).into(profile_image)
            } else {
                Picasso.get().load(it.profile).into(profile_image)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                startLoginActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state = "online"
        viewModel.updateState()
    }

    override fun onPause() {
        super.onPause()
        viewModel.state = "offline"
        viewModel.updateState()
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        var fragments: Fragment? = null
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> fragments = ChatFragment()
                1 -> fragments = SearchFragment()
                2 -> fragments = SettingFragment()
            }
            return fragments!!
        }
    }
}