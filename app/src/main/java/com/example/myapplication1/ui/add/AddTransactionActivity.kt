package com.example.myapplication1.ui.add

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication1.R
import com.example.myapplication1.data.model.Transaction
import com.example.myapplication1.data.model.TransactionType
import com.example.myapplication1.data.repository.TransactionStore
import com.google.firebase.auth.FirebaseAuth

class AddTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val amountEdit = findViewById<EditText>(R.id.amountEdit)
        val noteEdit = findViewById<EditText>(R.id.noteEdit)
        val typeRadioGroup = findViewById<RadioGroup>(R.id.typeRadioGroup)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val saveBtn = findViewById<Button>(R.id.saveButton)

        val categories = listOf("Еда", "Транспорт", "Развлечения", "Зарплата")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter

        saveBtn.setOnClickListener {
            val amount = amountEdit.text.toString().toDoubleOrNull()
            val note = noteEdit.text.toString()
            val type = when (typeRadioGroup.checkedRadioButtonId) {
                R.id.radioIncome -> TransactionType.INCOME
                else -> TransactionType.EXPENSE
            }
            val category = categorySpinner.selectedItem.toString()

            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Введите корректную сумму", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val transaction = Transaction(
                userId = userId,
                amount = amount,
                type = type,
                category = category,
                note = note
            )


            TransactionStore.transactions.add(transaction)
            Toast.makeText(this, "Транзакция добавлена!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
