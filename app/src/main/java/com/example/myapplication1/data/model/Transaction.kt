package com.example.myapplication1.data.model


import java.util.*

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    var amount: Double,
    var type: TransactionType,
    var category: String,
    var note: String,
    var currency: String = "₸",
    val date: Long = System.currentTimeMillis()
)


enum class TransactionType {
    INCOME,
    EXPENSE
}
