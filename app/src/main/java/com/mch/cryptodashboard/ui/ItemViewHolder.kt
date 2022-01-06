package com.mch.cryptodashboard.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.mch.cryptodashboard.R
import java.text.NumberFormat

class ItemViewHolder(
    parent: ViewGroup,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
) {

    private val icon = itemView.findViewById<ImageView>(R.id.icon)
    private val name = itemView.findViewById<TextView>(R.id.name)
    private val balance = itemView.findViewById<TextView>(R.id.balance)
    private val usd = itemView.findViewById<TextView>(R.id.usd)


    fun onBind(item: CurrencyItem) {
        Log.d(TAG, "onBind: $item")
        glide.load(item.imageUrl).into(icon)
        name.text = item.name
        balance.text = "${item.amount} ${item.symbol}"
        usd.text = item.balance?.let { "$ ${NumberFormat.getNumberInstance().format(it)}" } ?: "$ --"
    }


    companion object {
        private const val TAG = "CurrencyViewHolder"
    }
}