package com.example.whatsappclone

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.whatsappclone.fragments.ChatFragment
import com.example.whatsappclone.fragments.SearchFragment
import com.example.whatsappclone.fragments.SettingFragment
import com.example.whatsappclone.modelClass.Chat
import com.example.whatsappclone.modelClass.Users
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setting.view.*
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    var refUser: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                when (position){
                    0 -> tab.text = "Chat"
                    1 -> tab.text = "Search"
                    2 -> tab.text = "Profile"
                }
            }).attach()

        refUser!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    username.text = user!!.username
                    if(user.profile == ""){
                        Picasso.get().load(R.drawable.ic_profile).into(profile_image)
                    }
                    else {
                        Picasso.get().load(user.profile).into(profile_image)
                    }
                }
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
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun updateStatus(state: String) {
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["state"] = state
        ref.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
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