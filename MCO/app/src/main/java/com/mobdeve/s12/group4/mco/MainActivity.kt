package com.mobdeve.s12.group4.mco

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
import com.mobdeve.s12.group4.mco.adapters.AnalysisAdapter
import com.mobdeve.s12.group4.mco.adapters.CategoryChildAdapter
import com.mobdeve.s12.group4.mco.adapters.IconAdapter
import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.adapters.SpinnerAdapter
import com.mobdeve.s12.group4.mco.adapters.TransacChildAdapter
import com.mobdeve.s12.group4.mco.fragments.AnalysisFragment
import com.mobdeve.s12.group4.mco.fragments.CategoryFragment
import com.mobdeve.s12.group4.mco.fragments.HomeFragment
import com.mobdeve.s12.group4.mco.fragments.RecordsFragment
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction
import com.mobdeve.s12.group4.mco.utility.DataGenerator
import com.mobdeve.s12.group4.mco.utility.Filter
import com.mobdeve.s12.group4.mco.utility.PopupManager

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var toolbar : Toolbar
    private lateinit var addBtn : FloatingActionButton
    private lateinit var popupManager: PopupManager
    private lateinit var accountAdapter : AccountAdapter
    private lateinit var categoryAdapter: ParentAdapter<CategoryParent, Category>
    private lateinit var recordsAdapter: ParentAdapter<TransacParent, Transaction>
    private lateinit var analysisAdapter: AnalysisAdapter
    private lateinit var accountSpinnerAdapter: SpinnerAdapter<com.mobdeve.s12.group4.mco.models.Account>
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
        val categoryParent = DataGenerator.generateCategoryParent()
        val transacParent = DataGenerator.generateTransacParent()
        val icons = DataGenerator.generateIcons()

        val filter = Filter()

        iconAdapter = IconAdapter(icons)
        accountAdapter = AccountAdapter(accounts)
        accountSpinnerAdapter = SpinnerAdapter(this, accounts,
                                                { it.name },
                                                { it.imageId })
        categoryAdapter = ParentAdapter(categoryParent,
                                        {it.section},
                                        {it.list},
                                        { childList -> CategoryChildAdapter(childList) },
                                        filter)
        recordsAdapter = ParentAdapter(transacParent,
                                        {it.section},
                                        {it.transactions},
                                        { childList -> TransacChildAdapter(childList) },
                                        filter)
        analysisAdapter = AnalysisAdapter(categories)

        val homeFragment = HomeFragment(accountAdapter)
        val categoryFragment = CategoryFragment(categoryAdapter)
        val recordsFragment = RecordsFragment(recordsAdapter, filter)
        val analysisFragment = AnalysisFragment(analysisAdapter, filter)
        setCurrentFragment(homeFragment)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.record -> setCurrentFragment(recordsFragment)
                R.id.category -> setCurrentFragment(categoryFragment)
                R.id.analysis -> setCurrentFragment(analysisFragment)
            }
            true
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        popupManager = PopupManager(
                            this,
                            homeFragment,
                            recordsFragment,
                            categoryFragment,
                            analysisFragment,
                            accountAdapter,
                            recordsAdapter,
                            accountSpinnerAdapter,
                            iconAdapter,
                            categories,
                            )
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