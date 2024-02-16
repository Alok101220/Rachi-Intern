package com.example.rachi_intern.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rachi_intern.R
import com.example.rachi_intern.roomDb.entities.Product
import com.google.android.material.imageview.ShapeableImageView
import org.w3c.dom.Text
import java.util.Base64

class ProductAdapter(
    private var products: MutableList<Product>
) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private var isLoading = false

    private lateinit var onLoadMoreListener: OnLoadMoreListener

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        this.onLoadMoreListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, product: Product)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context: Context = itemView.context

        val productName = itemView.findViewById<TextView>(R.id.product_name)
        val productPrice = itemView.findViewById<TextView>(R.id.product_price)
        val productImage=itemView.findViewById<ShapeableImageView>(R.id.product_image)

        init {
            itemView.setOnClickListener {
                mListener?.onItemClick(adapterPosition, products[adapterPosition])
            }
        }
    }

    inner class LoadingViewHolder(itemView: View, private val parentRecyclerView: RecyclerView) :
        RecyclerView.ViewHolder(itemView) {

        init {
            (parentRecyclerView).addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as GridLayoutManager

                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                    if (isLoading && lastVisiblePosition >= products.size - 1) {
                        onLoadMoreListener.onLoadMore()
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_item, parent, false)
        return ViewHolder(view)

    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position == products.size) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return products.size + if (isLoading) 1 else 0
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=products[position]

        holder.productName.text=item.productName
        holder.productPrice.text="₹${item.productPrice}"
        val bitmap = BitmapFactory.decodeByteArray(item.productImage, 0, item.productImage.size)
        holder.productImage.setImageBitmap(bitmap)

    }

    fun updateList(newProducts: List<Product>, isNewSearch: Boolean) {
        if (!isNewSearch) {
            products.addAll(newProducts)
        }
        notifyDataSetChanged()
    }

    fun showLoading() {
        isLoading = true
        notifyDataSetChanged()
    }

    fun getIsLoading(): Boolean {
        return isLoading
    }

    fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }
}