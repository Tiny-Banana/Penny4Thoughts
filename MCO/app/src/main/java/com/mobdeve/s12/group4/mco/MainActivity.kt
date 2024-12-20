package com.mobdeve.s12.group4.mco

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.s12.group4.mco.adapters.AccountAdapter
import com.mobdeve.s12.group4.mco.adapters.AnalysisAdapter
import com.mobdeve.s12.group4.mco.adapters.BudgetChildAdapter
import com.mobdeve.s12.group4.mco.adapters.CategoryChildAdapter
import com.mobdeve.s12.group4.mco.adapters.IconAdapter
import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.adapters.SpinnerAdapter
import com.mobdeve.s12.group4.mco.adapters.TransacChildAdapter
import com.mobdeve.s12.group4.mco.fragments.AnalysisFragment
import com.mobdeve.s12.group4.mco.fragments.BudgetFragment
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
    private lateinit var budgetAdapter: ParentAdapter<CategoryParent, Category>
    private lateinit var recordsAdapter: ParentAdapter<TransacParent, Transaction>
    private lateinit var analysisAdapter: AnalysisAdapter
    private lateinit var accountSpinnerAdapter: SpinnerAdapter<com.mobdeve.s12.group4.mco.models.Account>
    private lateinit var categorySpinnerAdapter: SpinnerAdapter<Category>
    private lateinit var accIconAdapter: IconAdapter
    private lateinit var catIconAdapter: IconAdapter
    private lateinit var homeFragment: HomeFragment
    private lateinit var analysisFragment: AnalysisFragment
    private lateinit var recordsFragment: RecordsFragment
    private lateinit var categoryFragment: CategoryFragment
    private lateinit var budgetFragment: BudgetFragment
    private lateinit var filter: Filter
    private lateinit var budgetChildAdapter: BudgetChildAdapter
    private lateinit var transacChildAdapter: TransacChildAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        addBtn = findViewById(R.id.addBtn)

        setSupportActionBar(toolbar)

        filter = Filter()
        popupManager = PopupManager()

        val accounts = DataGenerator.generateAccountData()
        val categories = DataGenerator.generateCategoryData()
        val categoryParent = filter.filterCategoryParent(categories)
        val budgetParent = DataGenerator.generateBudgetParent(filter.selectedMonthNum, filter.selectedYear)
        val transacParent = DataGenerator.generateTransacParent()
        val accIcons = DataGenerator.generateAccIcons()
        val catIcons = DataGenerator.generateCatIcons()

        accIconAdapter = IconAdapter(accIcons)
        catIconAdapter = IconAdapter(catIcons)

        accountAdapter = AccountAdapter(
            this,
            accounts,
            accIconAdapter)
        accountSpinnerAdapter = SpinnerAdapter(
            this,
            accounts,
            { it.name },
            { it.imageId })
        categorySpinnerAdapter = SpinnerAdapter(
            this,
            ArrayList(),
            { it.name },
            { it.imageId }
        )

        categoryAdapter = ParentAdapter(categoryParent,
            {it.section},
            {it.list},
            { childList ->
                CategoryChildAdapter(
                    childList,
                    this,
                    categoryAdapter,
                    catIconAdapter,
                    filter) },
            filter,
            this,
            catIconAdapter,
            categorySpinnerAdapter)
        categorySpinnerAdapter.setCategoryAdapter(categoryAdapter)

        recordsAdapter = ParentAdapter(transacParent,
            {it.section},
            {it.list},
            { childList ->
                transacChildAdapter = TransacChildAdapter(
                    recordsAdapter,
                    childList,
                    filter,
                    this,
                    homeFragment,
                    recordsFragment,
                    categoryFragment,
                    analysisFragment,
                    budgetFragment,
                    accountAdapter,
                    recordsAdapter,
                    budgetAdapter,
                    accountSpinnerAdapter,
                    categorySpinnerAdapter)
                transacChildAdapter.setFragment(recordsFragment)
                transacChildAdapter
            },
            filter,
            this,
            catIconAdapter,
            categorySpinnerAdapter)
        budgetAdapter = ParentAdapter(
            budgetParent,
            { it.section },
            { it.list },
            { childList ->
                //save adapter so we can set the fragment in the budgetchildadapter
                budgetChildAdapter = BudgetChildAdapter(budgetAdapter, childList, filter)
                //will execute after the budgetfragment is initialized
                budgetChildAdapter.setFragment(budgetFragment)
                budgetChildAdapter
            },
            filter,
            this,
            catIconAdapter,
            categorySpinnerAdapter
        )

        analysisAdapter = AnalysisAdapter(categories)

        homeFragment = HomeFragment(accountAdapter)
        categoryFragment = CategoryFragment(categoryAdapter)
        recordsFragment = RecordsFragment(recordsAdapter, filter)
        analysisFragment = AnalysisFragment(analysisAdapter, filter)
        budgetFragment = BudgetFragment(budgetAdapter, filter)
        setCurrentFragment(homeFragment)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.record -> setCurrentFragment(recordsFragment)
                R.id.category -> setCurrentFragment(categoryFragment)
                R.id.analysis -> setCurrentFragment(analysisFragment)
                R.id.budget -> setCurrentFragment(budgetFragment)
            }
            true
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        addBtn.setOnClickListener {
            popupManager.showTransactionPopup(
                        this,
                        homeFragment,
                        recordsFragment,
                        categoryFragment,
                        analysisFragment,
                        budgetFragment,
                        accountAdapter,
                        recordsAdapter,
                        budgetAdapter,
                        accountSpinnerAdapter,
                        categorySpinnerAdapter,
                        filter,
                        null
                    )
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}