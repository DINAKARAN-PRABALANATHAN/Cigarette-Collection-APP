package com.cibinenterprizes.cibincollection

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.network_alart_dialog.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    var user = FirebaseAuth.getInstance().currentUser
    var database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        checkConnection()
        auth = FirebaseAuth.getInstance()

        val drawerLayout = findViewById<DrawerLayout>(R.id.home_drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.home_nav_view)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        var getData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var storeId = snapshot.child("Store ID").child(user?.uid.toString()).child("id").getValue().toString()
                var userName =
                    snapshot.child("User Details").child(storeId)
                        .child("username").getValue().toString()
                var storeName =
                    snapshot.child("User Details").child(storeId)
                        .child("storeName").getValue().toString()
                var mobile =
                    snapshot.child("User Details").child(storeId)
                        .child("mobile").getValue().toString()
                var collection =
                    snapshot.child("User Details").child(storeId)
                        .child("totalCollected").getValue().toString()
                var amount =
                    snapshot.child("User Details").child(storeId)
                        .child("totalAmount").getValue().toString()
                var id =
                    snapshot.child("User Details").child(storeId)
                        .child("storeId").getValue().toString()

                profile_store_id.setText(id)
                profile_username.setText(userName)
                profile_storename.setText(storeName)
                profile_mobile_number.setText(mobile)
                profile_collected.setText(collection)
                profile_amount.setText(amount)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        database.addValueEventListener(getData)
        database.addListenerForSingleValueEvent(getData)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_home -> home_drawer_layout.closeDrawer(GravityCompat.START)
            R.id.menu_price -> startActivity(Intent(this, Price::class.java))
            R.id.menu_delivery_report -> startActivity(Intent(this, Report::class.java))
            R.id.menu_about_us -> startActivity(Intent(this, AboutUs::class.java))
            R.id.menu_contact_support -> startActivity(Intent(this, ContactSupport::class.java))
            R.id.menu_terms_and_conditions -> startActivity(Intent(this, TermsAndConditions::class.java))
            R.id.menu_logout -> signoutOperation()
        }

        return true
    }
    private fun checkConnection() {

        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo

        if (null == networkInfo){
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.network_alart_dialog)

            dialog.setCanceledOnTouchOutside(false)

            dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.btn_try_again.setOnClickListener {
                recreate()
            }
            dialog.show()
        }
    }
    fun signoutOperation(){
        auth.signOut()
        startActivity(Intent(this,Login::class.java))
        finish()
    }
}