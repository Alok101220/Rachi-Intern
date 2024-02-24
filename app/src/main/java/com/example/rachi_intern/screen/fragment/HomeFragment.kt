import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.rachi_intern.R
import com.example.rachi_intern.screen.ViewProductActivity
import com.example.rachi_intern.adapter.ProductAdapter
import com.example.rachi_intern.factory.ProductViewModelFactory
import com.example.rachi_intern.roomDb.entities.Product
import com.example.rachi_intern.viewmodel.ProductViewModel
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment() {

    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var mainContainer: LinearLayout
    private lateinit var noResult: LinearLayout
    private lateinit var loadingBar: LottieAnimationView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productViewModel: ProductViewModel
    private val productList: MutableList<Product> = mutableListOf()
    private val searchList:MutableList<Product> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        val toolbar: MaterialToolbar = rootView.findViewById(R.id.appBar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        productViewModel = ViewModelProvider(
            this,
            ProductViewModelFactory(requireContext())
        )[ProductViewModel::class.java]

        homeRecyclerView = rootView.findViewById(R.id.searched_result_recyclerview)
        mainContainer = rootView.findViewById(R.id.searched_result_container)
        loadingBar = rootView.findViewById(R.id.product_loading)
        noResult = rootView.findViewById(R.id.no_result)


        addProductToList()
        productAdapter = ProductAdapter(productList)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        homeRecyclerView.adapter = productAdapter
        homeRecyclerView.layoutManager = layoutManager

        productAdapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, product: Product) {
                val intent = Intent(requireContext(), ViewProductActivity::class.java)
                intent.putExtra("product-id", productList[position].productId)
                startActivity(intent)
            }
        })

        return rootView
    }

    private fun addProductToList() {
        productViewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
            loadingBar.visibility = View.GONE
            if (products.isNullOrEmpty()) {
                noResult.visibility = View.VISIBLE
                mainContainer.visibility = View.GONE
            } else {
                noResult.visibility = View.GONE
                mainContainer.visibility = View.VISIBLE

                productList.clear()
                productList.addAll(products)
                productAdapter.updateList(productList, true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                handleSearchClick()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun handleSearchClick() {

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.search_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupWindow.isOutsideTouchable = true

        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val searchView:SearchView=popupView.findViewById(R.id.search_view)
        val searchFilterButton:ImageView=popupView.findViewById(R.id.filterButton)
        val searchRecyclerView = popupView.findViewById<RecyclerView>(R.id.search_popup_searched_result_recyclerview)
       val searchMainContainer = popupView.findViewById<LinearLayout>(R.id.search_popup_searched_result_container)
        val searchLoadingBar = popupView.findViewById<LottieAnimationView>(R.id.search_popup_product_loading)
        val searchNoResult = popupView.findViewById<LinearLayout>(R.id.search_popup_no_result)

        val searchAdapter=ProductAdapter(searchList)
        searchRecyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        searchRecyclerView.adapter=searchAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                if (query.isNullOrEmpty()) {
                    searchLoadingBar.visibility=View.GONE
                    searchMainContainer.visibility = View.GONE
                    searchNoResult.visibility = View.VISIBLE
                    searchList.clear()
                    searchAdapter.updateList(searchList,true)

                } else {
                    searchLoadingBar.visibility=View.VISIBLE
                    productViewModel.getProductsByName(query.toString()).observe(requireActivity()) { products ->
                        searchLoadingBar.visibility=View.GONE
                        if (products.isNullOrEmpty()) {
                            searchMainContainer.visibility = View.GONE
                            searchNoResult.visibility = View.VISIBLE
                        } else {
                            searchNoResult.visibility = View.GONE
                            searchMainContainer.visibility = View.VISIBLE
                            searchList.clear()
                            searchList.addAll(products)
                            searchAdapter.updateList(searchList, true)
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text change
                if (newText.isNullOrEmpty()) {
                    searchLoadingBar.visibility=View.GONE
                    searchMainContainer.visibility = View.GONE
                    searchNoResult.visibility = View.VISIBLE
                    searchList.clear()
                    searchAdapter.updateList(searchList,true)

                }else{
                    searchLoadingBar.visibility=View.VISIBLE
                    productViewModel.getProductsByName(newText.toString()).observe(requireActivity()) { products ->
                        if (products.isNullOrEmpty()) {
                            searchLoadingBar.visibility=View.GONE
                            searchMainContainer.visibility = View.GONE
                            searchNoResult.visibility = View.VISIBLE
                        } else {
                            searchNoResult.visibility = View.GONE
                            searchMainContainer.visibility = View.VISIBLE
                            searchList.clear()
                            searchList.addAll(products)
                            searchAdapter.updateList(searchList, true)
                        }
                    }

                }
                return true
            }
        })

        searchAdapter.setOnItemClickListener(object :ProductAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, product: Product) {
                val intent = Intent(requireContext(), ViewProductActivity::class.java)
                intent.putExtra("product-id",searchList[position].productId)
                startActivity(intent)
            }

        })



    }

}
