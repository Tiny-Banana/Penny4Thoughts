package com.mobdeve.s12.group4.mco

import android.accounts.Account
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.s12.group4.mco.adapters.AccountAdapter
import com.mobdeve.s12.group4.mco.adapters.IconAdapter
import com.mobdeve.s12.group4.mco.adapters.SpinnerAdapter
import com.mobdeve.s12.group4.mco.fragments.HomeFragment
import com.mobdeve.s12.group4.mco.fragments.RecordsFragment
import com.mobdeve.s12.group4.mco.models.Category

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var toolbar : Toolbar
    private lateinit var addBtn : FloatingActionButton
    private lateinit var popupManager: PopupManager
    private lateinit var accountAdapter : AccountAdapter
    private lateinit var accountSpinnerAdapter: SpinnerAdapter<com.mobdeve.s12.group4.mco.models.Account>
    private lateinit var categorySpinnerAdapter: SpinnerAdapter<Category>
    private lateinit var iconAdapter: IconAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        addBtn = findViewById(R.id.addBtn)

        setSupportActionBar(toolbar)

        val accounts = DataGenerator.generateAccountData()
        val categories = DataGenerator.generateCategoryData()
        val icons = DataGenerator.generateIcons()
        accountAdapter = AccountAdapter(accounts)
        accountSpinnerAdapter = SpinnerAdapter(this, accounts,
                                                { account -> account.name },
                                                { account -> account.imageId })
        categorySpinnerAdapter = SpinnerAdapter(this, categories,
                                                { category -> category.name },
                                                { category -> category.imageId })
        iconAdapter = IconAdapter(icons)

        val homeFragment = HomeFragment(accountAdapter)
        val recordsFragment = RecordsFragment()
        setCurrentFragment(homeFragment)


        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.record -> setCurrentFragment(recordsFragment)
            }
            true
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        popupManager = PopupManager(this, accountAdapter, accountSpinnerAdapter, categorySpinnerAdapter, iconAdapter)
        addBtn.setOnClickListener {
            showAddPopUp(it)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }


    private fun showAddPopUp(view: View) {
        // Create a PopupMenu
        val popupMenu = PopupMenu(this, view)

        // Inflate the menu resource into the popup menu
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.gravity = Gravity.END
        //Set click listeners for the popup menu items
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_new_account -> {
                    Toast.makeText(this, "Add New Account clicked", Toast.LENGTH_SHORT).show()
                    popupManager.showAddAcc()
                    true
                }
                R.id.add_transaction -> {
                    Toast.makeText(this, "Add Transaction clicked", Toast.LENGTH_SHORT).show()
                    popupManager.showAddTransaction()
                    true
                }
                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}