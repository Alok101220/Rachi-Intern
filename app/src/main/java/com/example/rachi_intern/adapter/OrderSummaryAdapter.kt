package com.example.rachi_intern.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rachi_intern.R
import com.example.rachi_intern.dao.OrderProduct
import com.google.android.material.imageview.ShapeableImageView

class OrderSummaryAdapter (private var orderProducts: List<OrderProduct>): RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderProductImg:ShapeableImageView=itemView.findViewById(R.id.order_summary_item_product_img)
        val orderProductName:TextView=itemView.findViewById(R.id.order_summary_item_product_name)
        val orderProductPrice:TextView=itemView.findViewById(R.id.order_summary_item_product_price)
        val orderProductQuantity:TextView=itemView.findViewById(R.id.order_summary_item_product_quantity)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_summary_recyclerview_list_item, parent, false)

        // Pass the longClickListener instance to the ViewHolder
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return orderProducts.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = orderProducts[position]
        holder.orderProductName.text =item.product.productName
        holder.orderProductPrice.text="₹${item.product.productPrice}"
        holder.orderProductQuantity.text=item.quantity.toString()

        val bitmap = BitmapFactory.decodeByteArray(item.product.productImage, 0, item.product.productImage.size)
        holder.orderProductImg .setImageBitmap(bitmap)

    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(certificate:List<OrderProduct>){
        orderProducts=certificate
        notifyDataSetChanged()
    }

    fun getTotalPrice(): Double {
        var totalPrice = 0.0
        for (orderProduct in orderProducts) {
            val productPrice = orderProduct.product.productPrice.substring(1).toDouble()
            totalPrice += productPrice * orderProduct.quantity
        }
        return totalPrice
    }
}
