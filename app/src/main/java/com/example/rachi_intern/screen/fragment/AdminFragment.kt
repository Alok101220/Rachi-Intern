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
import androidx.appcompat.widget.SearchView
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
    private lateinit var updateProductPopup: RelativeLayout
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
        updateProductPopup=rootView.findViewById(R.id.updateProduct_container)
        logoutAdmin = rootView.findViewById(R.id.logout_admin)

        addProductPopup.setOnClickListener {
            addProductPopup(null)
        }
        updateProductPopup.setOnClickListener {
            updateProductPopup()
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

    private fun addProductPopup(product: Product?) {

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.add_product_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupWindow.isOutsideTouchable = true

        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val backButton:ImageView = popupView.findViewById(R.id.back_from_add_product)
        val productName: TextInputEditText = popupView.findViewById(R.id.add_product_name)
        val productCategory: Spinner = popupView.findViewById(R.id.add_product_category)
        val productPrice: TextInputEditText = popupView.findViewById(R.id.add_product_price)
        val productDescription: TextInputEditText = popupView.findViewById(R.id.add_product_description)
        val productImageSelector: ImageView = popupView.findViewById(R.id.add_product_image)
        productSelectedImage = popupView.findViewById(R.id.add_product_selected_image)

        val addProductButton: MaterialButton = popupView.findViewById(R.id.add_product_add_button)
        val addProductLoading: LottieAnimationView =
            popupView.findViewById(R.id.add_product_loading)

        val removeProductContainer:RelativeLayout = popupView.findViewById(R.id.remove_product_container)
        val removeProductButton:MaterialButton = popupView.findViewById(R.id.product_remove_button)

        var selectedCategory = ""


        if (product!=null){
            productName.setText(product.productName)
            productPrice.setText(product.productPrice)
            productDescription.setText(product.productDescription)
            selectedCategory=product.productCategory
            selectedImageByte=product.productImage
            removeProductContainer.visibility=View.VISIBLE
            val bitmap = BitmapFactory.decodeByteArray(product.productImage, 0, product.productImage.size)

            productSelectedImage.visibility = View.VISIBLE
            productSelectedImage.setImageBitmap(bitmap)
        }
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
        removeProductButton.setOnClickListener {
            productViewModel.deleteProduct(product!!)
        }
        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    private fun updateProductPopup(){

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.update_product_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupWindow.isOutsideTouchable = true

        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val updateRecyclerView:RecyclerView=popupView.findViewById(R.id.update_product_searched_result_recyclerview)
        val searchView:SearchView=popupView.findViewById(R.id.search_view)
        val updateProductSearchedResultContainer:LinearLayout=popupView.findViewById(R.id.update_product_searched_result_container)
        val backButton:ImageView=popupView.findViewById(R.id.back_from_update_product)
        val noResultFound:LinearLayout=popupView.findViewById(R.id.update_product_no_result)


        val updateProductList:MutableList<Product> = mutableListOf()
        val updateProductAdapter=ProductAdapter(updateProductList)
        updateRecyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        updateRecyclerView.adapter=updateProductAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query.isNullOrEmpty()){
                    noResultFound.visibility=View.VISIBLE
                    updateProductSearchedResultContainer.visibility=View.GONE

                    updateProductList.clear()
                    updateProductAdapter.updateList(updateProductList,true)
                }else{
                    productViewModel.getProductsByName(query.toString()).observe(requireActivity()){products->
                        if(products.isNullOrEmpty()){
                            noResultFound.visibility=View.VISIBLE
                            updateProductSearchedResultContainer.visibility=View.GONE
                        }else{
                            noResultFound.visibility=View.GONE
                            updateProductSearchedResultContainer.visibility=View.VISIBLE
                            updateProductList.clear()
                            updateProductList.addAll(products)
                            updateProductAdapter.updateList(updateProductList,true)
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        updateProductAdapter.setOnItemClickListener(object :ProductAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, product: Product) {
                addProductPopup(product)
            }

        })



        backButton.setOnClickListener {
            popupWindow.dismiss()
        }




    }


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