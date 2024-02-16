import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.rachi_intern.R
import com.example.rachi_intern.ViewProductActivity
import com.example.rachi_intern.adapter.ProductAdapter
import com.example.rachi_intern.factory.ProductViewModelFactory
import com.example.rachi_intern.roomDb.entities.Product
import com.example.rachi_intern.viewmodel.ProductViewModel
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment() {

    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var mainContainer:LinearLayout
    private lateinit var loadingBar:LottieAnimationView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productViewModel: ProductViewModel
    private val productList: MutableList<Product> = mutableListOf()

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
        mainContainer=rootView.findViewById(R.id.searched_result_container)
        loadingBar=rootView.findViewById(R.id.product_loading)
        addProductToList()
        productAdapter = ProductAdapter(productList)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        homeRecyclerView.adapter = productAdapter
        homeRecyclerView.layoutManager = layoutManager


        productAdapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, product: Product) {
                val intent = Intent(requireContext(), ViewProductActivity::class.java)
                intent.putExtra("product-id",productList[position].productId)
                startActivity(intent)
            }

        })

        return rootView
    }

    private fun addProductToList() {
        productViewModel.getAllProducts().observe(viewLifecycleOwner) { products ->
            loadingBar.visibility = View.GONE
            if (products != null) {
                mainContainer.visibility = View.VISIBLE

                productList.clear()

                productList.addAll(products)
                productAdapter.updateList(productList, true)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query.isNullOrEmpty()){

                }else{
                    productViewModel.getProductsByName(query.toString()).observe(viewLifecycleOwner){products->
                        if(products.isNullOrEmpty()){

                        }else{
                            productList.clear()
                            productList.addAll(products)
                            productAdapter.updateList(productList,true)
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty()){
                    productViewModel.getAllProducts().observe(viewLifecycleOwner){products->
                        if(products.isNullOrEmpty()){

                        }else{
                            productList.clear()
                            productList.addAll(products)
                            productAdapter.updateList(productList,true)
                        }
                    }
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }
}
