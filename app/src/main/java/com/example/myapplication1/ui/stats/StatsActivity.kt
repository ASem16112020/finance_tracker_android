package com.example.myapplication1.ui.stats


import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication1.R
import com.example.myapplication1.data.model.TransactionType
import com.example.myapplication1.data.repository.TransactionStore
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val barChart = findViewById<BarChart>(R.id.barChart)

        val transactions = TransactionStore.transactions

        // ========== PIE CHART: Расходы по категориям ==========
        val expenseMap = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val pieEntries = expenseMap.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }

        val pieDataSet = PieDataSet(pieEntries, "Расходы по категориям")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        pieChart.data = PieData(pieDataSet)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.description = Description().apply { text = "" }
        pieChart.invalidate()

        // ========== BAR CHART: Доходы и расходы ==========
        val incomeTotal = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenseTotal = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val barEntries = listOf(
            BarEntry(0f, incomeTotal.toFloat()),
            BarEntry(1f, expenseTotal.toFloat())
        )

        val barDataSet = BarDataSet(barEntries, "Общая сумма")
        barDataSet.colors = listOf(Color.GREEN, Color.RED)

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Доход", "Расход"))
        barChart.description = Description().apply { text = "" }
        barChart.invalidate()
    }
}
