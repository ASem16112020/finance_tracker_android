package com.example.myapplication1.data.model


import java.util.*

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val note: String,
    val date: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
