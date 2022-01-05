package com.mch.cryptodashboard.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mch.cryptodashboard.R
import java.math.BigDecimal
import java.text.NumberFormat

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel by viewModels<MainViewModel>()

    private val adapter by lazy { ItemAdapter(Glide.with(this)) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter

        val totalBalance = view.findViewById<TextView>(R.id.balance)

        viewModel.list.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            var sum = BigDecimal(0)
            list.forEach { item ->
                item.usd?.also { sum += it }
            }
            totalBalance.text = "$ ${NumberFormat.getNumberInstance().format(sum)} USD"
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}