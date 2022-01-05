package com.mch.cryptodashboard.ui

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mch.cryptodashboard.data.Coin

class CoinAdapter : ListAdapter<Coin, CoinViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        return CoinViewHolder(parent)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
    }


    companion object {
        private const val TAG = "CoinAdapter"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Coin>() {

            override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
                return oldItem == newItem
            }
        }
    }
}