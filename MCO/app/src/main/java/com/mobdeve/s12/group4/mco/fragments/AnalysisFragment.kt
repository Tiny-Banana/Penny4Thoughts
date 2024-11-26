package com.mobdeve.s12.group4.mco.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.adapters.AnalysisAdapter
import com.mobdeve.s12.group4.mco.utility.BalanceCalculator
import com.mobdeve.s12.group4.mco.utility.CustomMarkerView
import com.mobdeve.s12.group4.mco.utility.Filter
import java.text.DecimalFormat
import java.util.Calendar

class AnalysisFragment(
    private val analysisAdapter: AnalysisAdapter,
    private val filter: Filter
) : Fragment(R.layout.fragment_analysis) {

    private lateinit var expenseTV: TextView
    private lateinit var incomeTV: TextView
    private lateinit var analysisRV: RecyclerView
    private lateinit var date: TextView
    private lateinit var ivBracketLeft: ImageView
    private lateinit var ivBracketRight: ImageView
    private lateinit var pieChart: PieChart
    private lateinit var spinner: Spinner
    private lateinit var divider: View

    private var currentMonth: Int = 0
    private var currentYear: Int = 0
    private var selectedMonthName = "Jan"

    private val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupSpinner()
        setupDateNavigation()

        val calendar = Calendar.getInstance()
        currentMonth = calendar.get(Calendar.MONTH) // 0-based month (January = 0)
        currentYear = calendar.get(Calendar.YEAR)

        filterByMonth(currentMonth, currentYear)
        updateBalance()
        updatePieChart()
    }

    private fun initializeViews(view: View) {
        expenseTV = view.findViewById(R.id.monthlyExpense)
        incomeTV = view.findViewById(R.id.monthlyIncome)
        analysisRV = view.findViewById(R.id.analysisRV)
        ivBracketLeft = view.findViewById(R.id.ivBracket1)
        ivBracketRight = view.findViewById(R.id.ivBracket2)
        date = view.findViewById(R.id.date)
        pieChart = view.findViewById(R.id.pieChart)
        spinner = view.findViewById(R.id.spin_category_type)
        divider = view.findViewById(R.id.analysisDivider)
    }

    private fun setupRecyclerView() {
        analysisRV.adapter = analysisAdapter
        analysisRV.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_type,
            R.layout.item_spinner_text
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.dropdown_spinner_text)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                when (selectedItem) {
                    "Income Overview" -> analysisAdapter.selectedType = "Income"
                    "Expense Overview" -> analysisAdapter.selectedType = "Expense"
                }

                updateFilterPieChart()
                updateBalance()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setupDateNavigation() {
        ivBracketLeft.setOnClickListener {
            navigateMonth(-1)
        }

        ivBracketRight.setOnClickListener {
            navigateMonth(1)
        }
    }

    private fun navigateMonth(monthDelta: Int) {
        currentMonth += monthDelta
        if (currentMonth < 0) {
            currentMonth = 11
            currentYear -= 1
        } else if (currentMonth > 11) {
            currentMonth = 0
            currentYear += 1
        }
        filterByMonth(currentMonth, currentYear)
        updateBalance()
        updatePieChart()
    }

    private fun filterByMonth(currentMonth: Int, currentYear: Int) {
        selectedMonthName = monthNames[currentMonth].substring(0, 3)
        date.text = "$selectedMonthName $currentYear"
        filter.setSelectedDate(selectedMonthName, currentYear)
        filter.applyFilter(currentMonth, currentYear, analysisAdapter)
    }

    private fun updatePieChart() {
        val filteredTransactions = analysisAdapter.filteredTransactions

        if (filteredTransactions.isEmpty()) {
            // Show the "No Data" message and hide the pie chart
            view?.findViewById<TextView>(R.id.noDataText)?.visibility = View.VISIBLE
            pieChart.visibility = View.GONE
            divider.visibility = View.GONE
        } else {
            // Hide the "No Data" message and show the pie chart
            view?.findViewById<TextView>(R.id.noDataText)?.visibility = View.GONE
            pieChart.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE

            val categoryTotals = analysisAdapter.aggregateTransactionsByCategory(filteredTransactions)
            val totalAmount = categoryTotals.values.sum()

            val categoryPercentages = analysisAdapter.calculateCategoryPercentages(categoryTotals, totalAmount)

            val pieEntries = categoryPercentages.map { (category, totalAmount) ->
                PieEntry(totalAmount.toFloat(), category)
            }

            setupPieChart(pieEntries)
        }
    }

    fun updateFilterPieChart() {
        if (!this::analysisRV.isInitialized) return

        filter.applyFilter(currentMonth, currentYear, analysisAdapter)
        updatePieChart()
    }

    private fun setupPieChart(pieEntries: List<PieEntry>) {
        if (pieEntries.isNotEmpty()) {
            val pieDataSet = PieDataSet(pieEntries, "")
            pieDataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
            pieDataSet.setDrawValues(false)

            val pieData = PieData(pieDataSet)

            val markerView = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
            pieChart.marker = markerView

            val regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
            val gray = ContextCompat.getColor(requireContext(), R.color.primary_gray)

            pieChart.apply {
                data = pieData
                highlightValues(null)
                description.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 60f
                transparentCircleRadius = 45f
                dragDecelerationFrictionCoef = 0.95f
                isRotationEnabled = true
                setHoleColor(Color.TRANSPARENT)
                setDrawEntryLabels(false)

                centerText = analysisAdapter.selectedType
                setCenterTextColor(gray)
                setCenterTextSize(16f)
                setCenterTextTypeface(regular)

                legend.apply {
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    isWordWrapEnabled = true
                    textSize = 12f
                    form = Legend.LegendForm.CIRCLE
                    typeface = regular
                    textColor = gray
                }

                invalidate()
            }
        } else {
            // If there are no entries, clear the pie chart
            pieChart.apply {
                data = null
                highlightValues(null)
                invalidate()  // Refresh the chart to clear it
            }
        }
    }

    fun updateBalance() {
        if (!this::analysisRV.isInitialized) return

        filter.applyFilter(currentMonth, currentYear, analysisAdapter)
        val balanceCalc = BalanceCalculator(analysisAdapter.filteredDateTransactions)
        val decimalFormat = DecimalFormat("#,##0.00")
        expenseTV.text = decimalFormat.format(balanceCalc.calculateTotalExpenses())
        incomeTV.text = decimalFormat.format(balanceCalc.calculateTotalIncome())
    }
}
