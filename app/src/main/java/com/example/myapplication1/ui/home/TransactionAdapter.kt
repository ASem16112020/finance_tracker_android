package com.example.myapplication1.ui.home


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication1.R
import com.example.myapplication1.data.model.Transaction
import com.example.myapplication1.data.model.TransactionType

class TransactionAdapter(private val items: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountText: TextView = view.findViewById(R.id.amountText)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val noteText: TextView = view.findViewById(R.id.noteText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = items[position]
        val sign = if (transaction.type.name == "INCOME") "+" else "-"
        holder.amountText.text = "$sign${transaction.amount} ₸"

        val color = if (transaction.type == TransactionType.INCOME) {
            Color.parseColor("#2E7D32") // зелёный
        } else {
            Color.parseColor("#C62828") // красный
        }

        holder.amountText.setTextColor(color)
        holder.categoryText.text = transaction.category
        holder.noteText.text = transaction.note
    }

    override fun getItemCount(): Int = items.size
}
