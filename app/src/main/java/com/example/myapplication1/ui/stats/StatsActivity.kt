package com.example.myapplication1.ui.stats

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication1.R
import com.example.myapplication1.data.model.TransactionType
import com.example.myapplication1.data.repository.TransactionStore
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val barChart = findViewById<BarChart>(R.id.barChart)
        val exportPdfButton = findViewById<Button>(R.id.exportPdfButton)

        // Берём только транзакции текущего пользователя
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val transactions = TransactionStore.transactions.filter { it.userId == uid }

        // Если нет данных — уведомляем
        if (transactions.isEmpty()) {
            Toast.makeText(this, "Нет данных для отображения", Toast.LENGTH_SHORT).show()
            return
        }

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

        // ========== Экспорт в PDF ==========
        exportPdfButton.setOnClickListener {
            exportReportToPdf(transactions)
        }
    }

    private fun exportReportToPdf(transactions: List<com.example.myapplication1.data.model.Transaction>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()
        paint.textSize = 12f

        var yPosition = 25

        canvas.drawText("Отчёт о транзакциях", 80f, yPosition.toFloat(), paint)
        yPosition += 30

        // 👉 Получаем выбранную валюту из настроек
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val defaultCurrency = sharedPreferences.getString("default_currency", "₸") ?: "₸"

        transactions.forEach {
            val text = "${if (it.type == TransactionType.INCOME) "Доход" else "Расход"}: ${String.format("%,.2f", it.amount)} $defaultCurrency, Категория: ${it.category}, Заметка: ${it.note}"
            canvas.drawText(text, 10f, yPosition.toFloat(), paint)
            yPosition += 20
        }

        pdfDocument.finishPage(page)

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, "report_${System.currentTimeMillis()}.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF сохранен: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка сохранения PDF", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }

}
