package com.example.myapplication1.ui.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication1.R
import com.example.myapplication1.data.model.Transaction
import com.example.myapplication1.data.model.TransactionType
import com.example.myapplication1.data.repository.TransactionStore
import com.google.firebase.auth.FirebaseAuth

class AddTransactionActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val savedSet = sharedPreferences.getStringSet("categories", setOf("Еда", "Транспорт", "Зарплата")) ?: setOf()

        val amountEdit = findViewById<EditText>(R.id.amountEdit)
        val noteEdit = findViewById<EditText>(R.id.noteEdit)
        val typeRadioGroup = findViewById<RadioGroup>(R.id.typeRadioGroup)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val saveBtn = findViewById<Button>(R.id.saveButton)

        val categories = savedSet.toMutableList()
        categories.sort()
        categories.add("+ Добавить категорию")

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = categoryAdapter

        val transactionId = intent.getStringExtra("transaction_id")
        val existingTransaction = TransactionStore.transactions.find { it.id == transactionId }

        if (existingTransaction != null) {
            amountEdit.setText(existingTransaction.amount.toString())
            noteEdit.setText(existingTransaction.note)
            if (existingTransaction.type == TransactionType.INCOME) {
                typeRadioGroup.check(R.id.radioIncome)
            } else {
                typeRadioGroup.check(R.id.radioExpense)
            }
            val categoryIndex = categories.indexOf(existingTransaction.category)
            if (categoryIndex >= 0) categorySpinner.setSelection(categoryIndex)
        }

        categorySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                if (categories[position] == "+ Добавить категорию") {
                    val editText = EditText(this@AddTransactionActivity)
                    editText.hint = "Введите название категории"
                    AlertDialog.Builder(this@AddTransactionActivity)
                        .setTitle("Новая категория")
                        .setView(editText)
                        .setPositiveButton("Добавить") { _, _ ->
                            val newCategory = editText.text.toString().trim()
                            if (newCategory.isNotEmpty() && !categories.contains(newCategory)) {
                                categories.add(categories.size - 1, newCategory)
                                categories.sort()
                                categories.add("+ Добавить категорию")
                                categoryAdapter.clear()
                                categoryAdapter.addAll(categories)
                                categoryAdapter.notifyDataSetChanged()
                                categorySpinner.setSelection(categories.indexOf(newCategory))
                                sharedPreferences.edit().putStringSet("categories",
                                    categories.filter { it != "+ Добавить категорию" }.toSet()
                                ).apply()
                            }
                        }
                        .setNegativeButton("Отмена", null)
                        .show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

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
            val defaultCurrency = sharedPreferences.getString("default_currency", "₸") ?: "₸"

            if (existingTransaction != null) {
                existingTransaction.amount = amount
                existingTransaction.type = type
                existingTransaction.category = category
                existingTransaction.note = note
                existingTransaction.currency = defaultCurrency
            } else {
                val transaction = Transaction(
                    userId = userId,
                    amount = amount,
                    type = type,
                    category = category,
                    note = note,
                    currency = defaultCurrency
                )
                TransactionStore.transactions.add(transaction)
            }

            Toast.makeText(this, "Транзакция сохранена!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}