package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.adapter.ProductAdapter
import com.kaizm.ecommerce_app.databinding.FragmentHomeBinding
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.viewmodel.AuthViewModel
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    
    private val authViewModel : AuthViewModel by activityViewModels()

    private lateinit var productFirstAdapter: ProductAdapter
    private lateinit var productSecondAdapter: ProductAdapter
    private lateinit var productThirdAdapter: ProductAdapter


    private val productViewModel: ProductViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillData()

        binding.userName.text = authViewModel.getUserName()

        binding.homeBtnSearch.setOnClickListener {
            val search = binding.homeSearch.text.toString()
            val bundle = Bundle().apply {
                putString("search", search)
            }
            findNavController().navigateUp()
            findNavController().navigate(R.id.action_homeFragment_to_listFragment, bundle)
        }


        binding.homeList1.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        productFirstAdapter = ProductAdapter(requireContext(), object : ProductAdapter.IClickItemProduct {
            override fun onClickToItem(product: Product) {
                val bundle = Bundle().apply { putParcelable("product", product) }
                findNavController().navigate(R.id.detailFragment, bundle)
            }
            override fun onClickAddItem(product: Product) {
                productViewModel.addToCart(requireContext(), product,1)
            }
        })
        binding.homeList1.adapter = productFirstAdapter

        binding.homeList2.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        productSecondAdapter = ProductAdapter(requireContext(), object : ProductAdapter.IClickItemProduct {
            override fun onClickToItem(product: Product) {
                val bundle = Bundle().apply { putParcelable("product", product) }
                findNavController().navigate(R.id.detailFragment, bundle)
            }
            override fun onClickAddItem(product: Product) {
                productViewModel.addToCart(requireContext(), product,1)
            }
        })

        binding.homeList2.adapter = productSecondAdapter

        binding.homeList3.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        productThirdAdapter = ProductAdapter(requireContext(), object : ProductAdapter.IClickItemProduct {
            override fun onClickToItem(product: Product) {
                val bundle = Bundle().apply { putParcelable("product", product) }
                findNavController().navigate(R.id.detailFragment, bundle)
            }
            override fun onClickAddItem(product: Product) {
                productViewModel.addToCart(requireContext(), product,1)
            }
        })

        binding.homeList3.adapter = productThirdAdapter
    }

    private fun fillData() {
        productViewModel.setFirstData(
            FirebaseDatabase.getInstance().getReference("products").orderByChild("time")
                .limitToLast(10)
        )
        productViewModel.productFirstLiveData.observe(viewLifecycleOwner) {
            productFirstAdapter.updateList(it)
        }

        productViewModel.setSecondData(
            FirebaseDatabase.getInstance().getReference("products").orderByChild("category")
                .startAt("Ram").limitToFirst(10)
        )
        productViewModel.productSecondLiveData.observe(viewLifecycleOwner) {
            productSecondAdapter.updateList(it)
        }

        productViewModel.setThirdData(
            FirebaseDatabase.getInstance().getReference("products").orderByChild("category")
                .startAt("Mouse").limitToFirst(10)
        )

        productViewModel.productThirdLiveData.observe(viewLifecycleOwner) {
            productThirdAdapter.updateList(it)
        }
    }
}