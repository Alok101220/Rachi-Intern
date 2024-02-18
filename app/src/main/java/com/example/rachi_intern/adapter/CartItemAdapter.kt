package com.example.rachi_intern.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rachi_intern.R
import com.example.rachi_intern.roomDb.entities.Product
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class CartItemAdapter(
    private var cartItems: List<Product>,
    private val context: Context
) : RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

    private var selectedCategory = "1"
    private var totalAmount: Double = 0.0
    private val quantitiesMap = mutableMapOf<Int, String>()

    interface OnRemoveItemClickListener {
        fun onRemoveItemClick(position: Int,quantity: String)
    }

    private var removeItemClickListener: OnRemoveItemClickListener? = null

    fun setOnRemoveItemClickListener(listener: OnRemoveItemClickListener) {
        removeItemClickListener = listener
    }

    interface OnBuyItemClickListener {
        fun onBuyItemClick(position: Int, quantity: String)
    }

    private var buyItemClickListener: OnBuyItemClickListener? = null

    fun setOnBuyItemClickListener(listener: OnBuyItemClickListener) {
        buyItemClickListener = listener
    }

    interface OnQuantitySelectedListener {
        fun onQuantitySelected(position: Int, quantity: String, totalAmount: Double)
    }

    private var quantitySelectedListener: OnQuantitySelectedListener? = null

    fun setOnQuantitySelectedListener(listener: OnQuantitySelectedListener) {
        quantitySelectedListener = listener
    }

    interface OnQuantityChangeListener {
        fun onQuantityChange(totalAmount: Double)
    }

    private var quantityChangeListener: OnQuantityChangeListener? = null

    fun setOnQuantityChangeListener(listener: OnQuantityChangeListener) {
        quantityChangeListener = listener
    }

    class ViewHolder(
        itemView: View,
        private val removeItemClickListener: OnRemoveItemClickListener?,
        private val buyItemClickListener: OnBuyItemClickListener?,
        private val quantitySelectedListener: OnQuantitySelectedListener?
    ) : RecyclerView.ViewHolder(itemView) {

        val cartItemImage: ShapeableImageView = itemView.findViewById(R.id.cart_item_product_img)
        val cartItemProductName: TextView = itemView.findViewById(R.id.cart_item_product_name)
        val cartItemProductPrice: TextView = itemView.findViewById(R.id.cart_item_product_price)
        val quantity: Spinner = itemView.findViewById(R.id.cart_item_product_quantity)
        val totalPrice: TextView = itemView.findViewById(R.id.cart_item_totalPrice)
        val removeCartItem: MaterialButton = itemView.findViewById(R.id.cart_item_product_remove)
        val buyCartItem: MaterialButton = itemView.findViewById(R.id.cart_item_product_buy)
        var selectedQuantity: String = "1"
        init {
            removeCartItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    removeItemClickListener?.onRemoveItemClick(position,this.selectedQuantity)
                }
            }
            buyCartItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    val selectedQuantity =this.selectedQuantity
                    buyItemClickListener?.onBuyItemClick(position, selectedQuantity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_recyclerview_list_item, parent, false)

        // Pass the longClickListener instance to the ViewHolder
        return ViewHolder(view, removeItemClickListener, buyItemClickListener, quantitySelectedListener)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cartItems[position]

        holder.cartItemProductName.text = item.productName
        holder.cartItemProductPrice.text = "₹${item.productPrice}"

        val bitmap = BitmapFactory.decodeByteArray(item.productImage, 0, item.productImage.size)
        holder.cartItemImage.setImageBitmap(bitmap)

        val options = listOf("1", "2", "3", "4", "5", "6")

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        holder.quantity.adapter = adapter

        holder.quantity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Handle the selected item
                val selectedOption = options[position]
                selectedCategory = selectedOption
                holder.totalPrice.text =
                    "₹${(item.productPrice.substring(0).toInt() * selectedCategory.toInt()).toString()}"

                // Update totalAmount
                updateTotalAmount()
                holder.selectedQuantity=selectedCategory
                setQuantity(item.productId.toInt(), selectedCategory)

                // Notify the listener about the selected quantity and total amount
                quantitySelectedListener?.onQuantitySelected(position, selectedCategory, totalAmount)
                quantityChangeListener?.onQuantityChange(totalAmount)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedCategory = options[0]
                holder.totalPrice.text =
                    (item.productPrice.toInt() * selectedCategory.toInt()).toString()

                // Update totalAmount
                updateTotalAmount()
                holder.selectedQuantity=selectedCategory
                setQuantity(item.productId.toInt(), selectedCategory)

                // Notify the listener about the selected quantity and total amount
                quantitySelectedListener?.onQuantitySelected(position, selectedCategory, totalAmount)
                quantityChangeListener?.onQuantityChange(totalAmount)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(certificate: List<Product>) {
        cartItems = certificate
        notifyDataSetChanged()

        // Update totalAmount when data is updated
        updateTotalAmount()
    }

    private fun updateTotalAmount() {
        totalAmount = cartItems.sumByDouble {
            it.productPrice.substring(0).toDouble() * selectedCategory.toInt()
        }
    }

    fun getTotalAmount(): Double {
        return totalAmount
    }

    fun setQuantity(position: Int, quantity: String) {
        quantitiesMap[position] = quantity
    }

    // Add this method to get a list of quantities
    fun getQuantities(): Map<Int,String> {
        return quantitiesMap

    }

}
