package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.adapter.ProductAdapter
import com.kaizm.ecommerce_app.databinding.FragmentListBinding
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel as ProductViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding
    lateinit var productAdapter: ProductAdapter

    private var search: String? = null

    private val viewModel: ProductViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        fillData(FirebaseDatabase.getInstance().getReference("products"))

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        binding.listCategory.adapter = adapter

        binding.listCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent!!.getItemAtPosition(position).toString() != "All") {
                    viewModel.filterProductByCategory(
                        parent.getItemAtPosition(position).toString()
                    )
                    viewModel.productListLiveData.observe(viewLifecycleOwner) {
                        productAdapter.updateList(it)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.listBtnPopular.setOnClickListener {
            fullData(FirebaseDatabase.getInstance().getReference("products").orderByChild("time"))
        }

        binding.listBtnPrice.setOnClickListener {
            if (binding.listBtnPrice.text == "Giá tăng") {
                viewModel.filterProductByPrice(false)
                binding.listBtnPrice.text = "Giá giảm"
            } else {
                viewModel.filterProductByPrice(true)
                binding.listBtnPrice.text = "Giá tăng"
            }
            viewModel.productListLiveData.observe(viewLifecycleOwner) {
                productAdapter!!.updateList(it)
            }
        }

        binding.listSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.filterProductBySearch(it) }
                viewModel.productListLiveData.observe(viewLifecycleOwner) {
                    productAdapter.updateList(it)
                }
                return true
            }
        })

        binding.rcList.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        binding.rcList.setHasFixedSize(true)


        productAdapter =
            ProductAdapter(requireContext(), object : ProductAdapter.IClickItemProduct {
                override fun onClickToItem(product: Product) {
                    val bundle = Bundle().apply { putParcelable("product", product) }
                    findNavController().navigate(
                        R.id.action_listFragment_to_detailFragment,
                        bundle
                    )
                }

                override fun onClickAddItem(product: Product) {
                    viewModel.addToCart(requireContext(), product, 1)
                }
            })
        binding.rcList.adapter = productAdapter

    }

    private fun fillData(query: Query) {
        search = arguments?.getString("search")
        if (search != null) {
            viewModel.clearCurrentProduct()
            viewModel.filterProductBySearch(search.toString())
            viewModel.productListLiveData.observe(viewLifecycleOwner) {
                productAdapter.updateList(it)
            }
        } else {
            fullData(query)
        }
    }

    private fun fullData(query: Query) {
        viewModel.setListData(query)
        viewModel.productListLiveData.observe(viewLifecycleOwner) {
            productAdapter.updateList(it)
        }
    }
}