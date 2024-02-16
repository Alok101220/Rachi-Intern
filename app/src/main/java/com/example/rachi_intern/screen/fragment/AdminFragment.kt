import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.rachi_intern.R
import com.example.rachi_intern.adapter.ProductAdapter
import com.example.rachi_intern.factory.ProductViewModelFactory
import com.example.rachi_intern.roomDb.entities.Product
import com.example.rachi_intern.screen.AuthenticationScreen
import com.example.rachi_intern.screen.fragment.PREF_FILE_NAME
import com.example.rachi_intern.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText


class AdminFragment : Fragment() {

    private lateinit var addProductPopup: RelativeLayout
    private lateinit var viewAllProduct: RelativeLayout
    private lateinit var logoutAdmin: RelativeLayout
    private lateinit var sharedPref: SharedPreferences

    private val productList: MutableList<Product> = mutableListOf()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productViewModel: ProductViewModel

    private val STORAGE_PERMISSION_CODE = 101

    private lateinit var productSelectedImage: ShapeableImageView
    private var selectedImageByte: ByteArray? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_admin, container, false)
        sharedPref = requireActivity().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

        productViewModel = ViewModelProvider(
            requireActivity(),
            ProductViewModelFactory(requireContext())
        )[ProductViewModel::class.java]


        addProductPopup = rootView.findViewById(R.id.addProduct_container)
        viewAllProduct = rootView.findViewById(R.id.viewAllProduct_container)
        logoutAdmin = rootView.findViewById(R.id.logout_admin)

        addProductPopup.setOnClickListener {
            addProductPopup()
        }
        viewAllProduct.setOnClickListener {
            viewAllProduct()
        }

        logoutAdmin.setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
            moveToLoginFragment()
        }

        return rootView
    }

    private fun moveToLoginFragment() {
        val intent = Intent(requireContext(), AuthenticationScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }

    private fun addProductPopup() {

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.add_product_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
//        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val productName: TextInputEditText = popupView.findViewById(R.id.add_product_name)
        val productCategory: Spinner = popupView.findViewById(R.id.add_product_category)
        val productPrice: TextInputEditText = popupView.findViewById(R.id.add_product_price)
        val productDescription: TextInputEditText = popupView.findViewById(R.id.add_product_price)
        val productImageSelector: ImageView = popupView.findViewById(R.id.add_product_image)
        productSelectedImage = popupView.findViewById(R.id.add_product_selected_image)

        val addProductButton: MaterialButton = popupView.findViewById(R.id.add_product_add_button)
        val addProductLoading: LottieAnimationView =
            popupView.findViewById(R.id.add_product_loading)
        var selectedCategory = ""

        productImageSelector.setOnClickListener {
            requestStoragePermission()
        }

        val options = listOf("wireless", "phone", "charger", "laptop")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        productCategory.adapter = adapter

        productCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Handle the selected item
                val selectedOption = options[position]
//                Toast.makeText(requireContext(), "Selected: $selectedOption", Toast.LENGTH_SHORT).show()
                selectedCategory = selectedOption
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedCategory = options[0]
            }
        }


        addProductButton.setOnClickListener {
            if (productName.text.isNullOrEmpty() || productCategory.isEmpty() || productPrice.text.isNullOrEmpty() || productDescription.text.isNullOrEmpty() || selectedImageByte == null) {
                Toast.makeText(requireContext(), "All field required!", Toast.LENGTH_SHORT)
                    .show()

            } else {
                val product = Product(
                    0,
                    productName.text.toString(),
                    "productCategory[0].toString()",
                    productPrice.text.toString(),
                    productDescription.text.toString(),
                    selectedImageByte!!
                )
                productViewModel.insertProduct(product)
                popupWindow.dismiss()
            }
        }

    }

    private fun viewAllProduct() {

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.view_all_product_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
//        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val recyclerView: RecyclerView =
            popupView.findViewById(R.id.all_product_searched_result_recyclerview)

        val recyclerViewContainer: LinearLayout =
            popupView.findViewById(R.id.all_product_searched_result_container)
        val loadingAnimation: LottieAnimationView =
            popupView.findViewById(R.id.viewAllProduct_loading)

        loadingAnimation.visibility = View.VISIBLE
        recyclerViewContainer.visibility = View.GONE

        productViewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
            loadingAnimation.visibility = View.GONE
            if (products != null) {
                recyclerViewContainer.visibility = View.VISIBLE
                productList.clear()
                productList.addAll(products)

            }
        }
        productAdapter = ProductAdapter(productList)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = layoutManager


    }
//
//    private fun addProductToList() {
//        productViewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
//            if (products != null) {
//                productList.clear()
//                productList.addAll(products)
//                productAdapter.updateList(productList,false)
//            }
//        }
//    }

    private fun requestStoragePermission() {
        if (!isStoragePermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf("android.permission.READ_EXTERNAL_STORAGE"),
                STORAGE_PERMISSION_CODE
            )
        } else {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        getContent.launch("image/*")
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

//                    val imageData :ByteArray = Base64.decode(bytes, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size)

                    productSelectedImage.visibility = View.VISIBLE
                    productSelectedImage.setImageBitmap(bitmap)
                    selectedImageByte = bytes

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            "android.permission.READ_EXTERNAL_STORAGE"
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun spinnerOption() {


    }

}