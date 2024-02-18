package com.example.rachi_intern.screen.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.rachi_intern.R
import com.example.rachi_intern.adapter.CartItemAdapter
import com.example.rachi_intern.adapter.OrderSummaryAdapter
import com.example.rachi_intern.dao.OrderProduct
import com.example.rachi_intern.factory.AddressViewModelFactory
import com.example.rachi_intern.factory.CartViewModelFactory
import com.example.rachi_intern.factory.OrderViewModelFactory
import com.example.rachi_intern.factory.ProductViewModelFactory
import com.example.rachi_intern.roomDb.entities.Address
import com.example.rachi_intern.roomDb.entities.Order
import com.example.rachi_intern.roomDb.entities.Product
import com.example.rachi_intern.roomDb.entities.User
import com.example.rachi_intern.viewmodel.AddressViewModel
import com.example.rachi_intern.viewmodel.CartViewModel
import com.example.rachi_intern.viewmodel.OrderViewModel
import com.example.rachi_intern.viewmodel.ProductViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.razorpay.Checkout
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.concurrent.atomic.AtomicInteger

class CartFragment : Fragment(), CartItemAdapter.OnQuantityChangeListener {

    private lateinit var cartMainContainer: NestedScrollView
    private lateinit var cartLoading: LottieAnimationView
    private lateinit var emptyCart: LottieAnimationView
    private lateinit var cartAddressContainer: LinearLayout
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var addAddress: MaterialButton

    private lateinit var addressContainer: LinearLayout
    private lateinit var addressUserName: TextView
    private lateinit var addressPinCode: TextView
    private lateinit var fullAddress: TextView
    private lateinit var changeAddressBtn: TextView

    private lateinit var totalPrice: TextView
    private lateinit var buyButton: MaterialButton

    private lateinit var sharedPref: SharedPreferences

    private lateinit var cartItemAdapter: CartItemAdapter

    private lateinit var cartViewModel: CartViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var orderViewModel: OrderViewModel

    private val productList: MutableList<Product> = mutableListOf()
    private val orderedProducts: MutableList<Order> = mutableListOf()

    private var currentUser: User? = null
    private var currentAddress: Address? = null

    private val RAZOR_PAY_REQUEST_CODE = 123 // Use any unique integer value

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_cart, container, false)

        sharedPref = requireActivity().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

        currentUser = fetchUserResponseFromSharedPreferences()

        cartViewModel = ViewModelProvider(
            requireActivity(),
            CartViewModelFactory(requireContext())
        )[CartViewModel::class.java]
        productViewModel = ViewModelProvider(
            requireActivity(),
            ProductViewModelFactory(requireContext())
        )[ProductViewModel::class.java]
        addressViewModel = ViewModelProvider(
            requireActivity(),
            AddressViewModelFactory(requireContext())
        )[AddressViewModel::class.java]
        orderViewModel = ViewModelProvider(
            requireActivity(),
            OrderViewModelFactory(requireContext())
        )[OrderViewModel::class.java]


        Checkout.preload(requireContext())

        cartMainContainer = rootView.findViewById(R.id.cart_main_container)
        cartLoading = rootView.findViewById(R.id.cart_loading)
        emptyCart=rootView.findViewById(R.id.cart_empty)
        cartAddressContainer = rootView.findViewById(R.id.cart_address_container)
        cartRecyclerView = rootView.findViewById(R.id.cart_recyclerView)
        addAddress = rootView.findViewById(R.id.cart_add_address)

        addressContainer = rootView.findViewById(R.id.cart_address_container)
        addressUserName = rootView.findViewById(R.id.deliverTo_name)
        addressPinCode = rootView.findViewById(R.id.deliverTo_pincode)
        fullAddress = rootView.findViewById(R.id.full_address)
        changeAddressBtn = rootView.findViewById(R.id.change_address_button)

        totalPrice = rootView.findViewById(R.id.cart_total_price)
        buyButton = rootView.findViewById(R.id.cart_buy_button)


        cartItemAdapter = CartItemAdapter(productList, requireContext())
        cartItemAdapter.setOnQuantityChangeListener(this)
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartRecyclerView.adapter = cartItemAdapter


        cartLoading.visibility = View.VISIBLE

        if (currentUser != null) {
            addressViewModel.getAddressByUserId(currentUser!!.userId)
                .observe(viewLifecycleOwner) { address ->
                    cartLoading.visibility = View.GONE
                    cartMainContainer.visibility = View.VISIBLE
                    if (address != null) {
                        if (address.isNotEmpty()) {
                            addressContainer.visibility = View.VISIBLE
                            addressUserName.text = currentUser!!.fullName
                            addressPinCode.text = address[0].pinCode
                            fullAddress.text =
                                "${address[0].locality} ${address[0].city} ${address[0].state}"
                            currentAddress = address[0]
                        } else {
                            addAddress.visibility = View.VISIBLE
                        }
                    }
                }
            cartViewModel.getCartItemByUser(currentUser!!.userId)
                .observe(viewLifecycleOwner) { cartItems ->

                    cartLoading.visibility = View.GONE
                    cartMainContainer.visibility = View.VISIBLE

                    if (cartItems.isNullOrEmpty()) {
                        emptyCart.visibility=View.VISIBLE
                    } else {
                        emptyCart.visibility=View.GONE

                        val productCounter = AtomicInteger(cartItems.size)
                        for (cartItem in cartItems) {

                            productViewModel.getProductById(cartItem.productId)
                                .observe(viewLifecycleOwner) { product ->
                                    if (product != null) {
                                        productList.add(product)
                                        productCounter.decrementAndGet()


                                        if (productCounter.get() == 0) {
                                            cartItemAdapter.updateData(productList)
                                        }
                                    }
                                }
                        }
                    }
                }
        }
        changeAddressBtn.setOnClickListener {
            addOrUpdateAddress(currentAddress)
        }
        addAddress.setOnClickListener {
            addOrUpdateAddress(null)
        }
        cartItemAdapter.setOnRemoveItemClickListener(object :
            CartItemAdapter.OnRemoveItemClickListener {
            override fun onRemoveItemClick(position: Int,quantity:String) {

                totalPrice.text="₹${totalPrice.text.toString().substring(1).toDouble()-productList[position].productPrice.toDouble()*quantity.toInt()}"
                cartViewModel.getCartItemByUserAndProduct(
                    currentUser!!.userId,
                    productList[position].productId
                )
                    .observe(viewLifecycleOwner) { cartItem ->
                        if (cartItem != null) {
                            cartViewModel.deleteCartItem(cartItem)
                            productList.removeAt(position)
                            cartItemAdapter.notifyItemRemoved(position)
                            cartItemAdapter.notifyItemRangeChanged(position, productList.size)
                        }
                    }
            }
        })

        cartItemAdapter.setOnBuyItemClickListener(object : CartItemAdapter.OnBuyItemClickListener {
            override fun onBuyItemClick(position: Int, quantity: String) {

                if (currentAddress == null) {
                    Toast.makeText(requireContext(), "Enter address!", Toast.LENGTH_SHORT).show()

                } else {
                    val orderItem = OrderProduct(
                        productList[position],
                        currentAddress!!,
                        currentUser!!.userId,
                        quantity.toInt()
                    )
                    openOrderSummary(listOf(orderItem))
                }

            }

        })

        buyButton.setOnClickListener {

            if (currentAddress == null) {
                Toast.makeText(requireContext(), "Enter address!", Toast.LENGTH_SHORT).show()

            }else if(productList.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Nothing in cart!", Toast.LENGTH_SHORT).show()
            }
            else {
                val orderProductList = mutableListOf<OrderProduct>()
                val quantities = cartItemAdapter.getQuantities()
                var i = 0
                for (product in productList) {
                    orderProductList.add(
                        OrderProduct(
                            product, currentAddress!!, currentUser!!.userId,
                            quantities[product.productId.toInt()].toString().toInt()
                        )
                    )
                    orderedProducts.add(
                        Order(
                            0,
                            currentUser!!.userId,
                            product.productId,
                            currentAddress!!.addressId,
                            quantities[product.productId.toInt()].toString().toLong(),
                            "Online",
                            currentAddress!!.toString()
                        )
                    )
                    i = i + 1
                }
                openOrderSummary(orderProductList)
            }
        }



        return rootView
    }

    private fun openOrderSummary(orderProductList: List<OrderProduct>) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.order_confirmation_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val orderSummaryAdapter = OrderSummaryAdapter(orderProductList)

        val backButton: ImageView = popupView.findViewById(R.id.back_from_order_confirmation)
        val recyclerView: RecyclerView = popupView.findViewById(R.id.order_summary_recyclerView)
        val totalPrice: TextView = popupView.findViewById(R.id.order_summary_total_price)
        val paymentButton: MaterialButton =
            popupView.findViewById(R.id.order_summary_payment_button)


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = orderSummaryAdapter

        val totalOrderPrice = orderSummaryAdapter.getTotalPrice()
        totalPrice.text = totalOrderPrice.toString()


        paymentButton.setOnClickListener {
            startRazorpayPayment(totalOrderPrice)
        }
        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    private fun fetchUserResponseFromSharedPreferences(): User? {
        val userJson = sharedPref.getString("user_details", null)
        val gson = Gson()
        if (userJson == null) return null;
        return gson.fromJson(userJson, User::class.java)
    }


    private fun addOrUpdateAddress(address: Address?) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.add_address_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val title: TextView = popupView.findViewById(R.id.add_address_title)
        val backButton: ImageView = popupView.findViewById(R.id.back_from_add_address)
        val addressLocality: TextInputEditText = popupView.findViewById(R.id.address_locality)
        val addressCity: TextInputEditText = popupView.findViewById(R.id.address_city)
        val addressPinCode: TextInputEditText = popupView.findViewById(R.id.address_pinCode)
        val addressState: TextInputEditText = popupView.findViewById(R.id.address_state)
        val addressPhoneNumber: TextInputEditText = popupView.findViewById(R.id.address_phoneNumber)
        val addOrUpdateAddress: MaterialButton = popupView.findViewById(R.id.address_add_button)
        val addAddressLoading: LottieAnimationView =
            popupView.findViewById(R.id.address_add_loading)

        if (address != null) {
            title.text = "Update address"
            addressCity.setText(address.city)
            addressLocality.setText(address.locality)
            addressPinCode.setText(address.pinCode)
            addressState.setText(address.state)
            addressPhoneNumber.setText(address.phoneNumber)
        } else {
            title.text = "Add address"
        }

        addOrUpdateAddress.setOnClickListener {
            if (addressCity.text.isNullOrEmpty() || addressLocality.text.isNullOrEmpty() || addressPinCode.text.isNullOrEmpty() || addressCity.text.isNullOrEmpty() || addressState.text.isNullOrEmpty() || addressPhoneNumber.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "All Fields required!", Toast.LENGTH_SHORT).show()

            } else {

                if (address != null) {
                    val newAddress = Address(
                        address.addressId,
                        addressLocality.text.toString(),
                        addressCity.text.toString(),
                        addressState.text.toString(),
                        addressPinCode.text.toString(),
                        addressPhoneNumber.text.toString(),
                        currentUser!!.userId
                    )
                    addressViewModel.updateAddress(newAddress)
                    popupWindow.dismiss()
                } else {
                    val newAddress = Address(
                        0,
                        addressLocality.text.toString(),
                        addressCity.text.toString(),
                        addressState.text.toString(),
                        addressPinCode.text.toString(),
                        addressPhoneNumber.text.toString(),
                        currentUser!!.userId
                    )
                    addressViewModel.insertAddress(newAddress)
                    popupWindow.dismiss()
                }
            }

        }
        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    override fun onQuantityChange(totalAmount: Double) {
        totalPrice.text = "₹$totalAmount"

    }

    private fun startRazorpayPayment(amount: Double) {
        try {
            val options = prepareRazorpayOptions(amount)
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_ldre7oeTXdAMDW")
            checkout.open(requireActivity(), options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun prepareRazorpayOptions(amount: Double): JSONObject {
        val options = JSONObject()
        options.put("name", "Your Store")
        options.put("description", "Payment for your order")
        options.put("currency", "INR")
        options.put("amount", amount * 100)

        val prefill = JSONObject()
        prefill.put("email", currentUser!!.email)
        prefill.put("contact", currentAddress!!.phoneNumber)

        options.put("prefill", prefill)

        return options
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RAZOR_PAY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Payment successful
                val outputResponse = data?.getStringExtra("response")
                handlePaymentSuccess(outputResponse)

                for (order in orderedProducts) {
                    orderViewModel.insertOrder(order)
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Payment canceled by the user
                handlePaymentCancelled()
            } else {
                // Payment failed
                handlePaymentFailed()
            }
        }
    }

    private fun handlePaymentSuccess(response: String?) {
        showToast("Payment Successful")
    }

    private fun handlePaymentCancelled() {
        showToast("Payment Cancelled")
    }

    private fun handlePaymentFailed() {
        showToast("Payment Failed")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}