package com.example.rachi_intern.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rachi_intern.R
import com.example.rachi_intern.dao.OrderProduct
import com.example.rachi_intern.roomDb.entities.Order
import com.example.rachi_intern.roomDb.entities.Product
import com.google.android.material.imageview.ShapeableImageView
import org.w3c.dom.Text

class OrderHistoryAdapter (private var orderList: List<Product>): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val orderedProductName:TextView=itemView.findViewById(R.id.order_history_item_product_name)
        val orderedProductPrice:TextView=itemView.findViewById(R.id.order_history_item_product_price)
        val orderedProductImg:ShapeableImageView=itemView.findViewById(R.id.order_history_item_product_img)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_recyclerview_list_item, parent, false)

        // Pass the longClickListener instance to the ViewHolder
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return orderList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = orderList[position]
        holder.orderedProductName.text=item.productName
        holder.orderedProductPrice.text="₹${item.productPrice}"
        val bitmap = BitmapFactory.decodeByteArray(item.productImage, 0, item.productImage.size)
        holder.orderedProductImg .setImageBitmap(bitmap)

    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newOrderList:List<Product>){
        orderList=newOrderList
        notifyDataSetChanged()
    }

}
