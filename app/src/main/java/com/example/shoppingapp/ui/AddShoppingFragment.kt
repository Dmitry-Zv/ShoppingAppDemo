package com.example.shoppingapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentAddShoppingItemBinding
import com.example.shoppingapp.others.Resource
import com.example.shoppingapp.others.collectFlow
import com.example.shoppingapp.ui.events.ViewModelEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddShoppingFragment @Inject constructor(
    private val glide: RequestManager
) : Fragment() {

    private var _binding: FragmentAddShoppingItemBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: ShoppingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddShoppingItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ShoppingViewModel::class.java]
        subscribeToObserver()
        with(binding) {
            btnAddShoppingItem.setOnClickListener {
                viewModel.obtainedEvent(
                    event = ViewModelEvent.InsertShoppingItem(
                        name = etShoppingItemName.text.toString(),
                        amountString = etShoppingItemAmount.text.toString(),
                        priceString = etShoppingItemPrice.text.toString()
                    )
                )
//                popBackStack()
            }
            binding.ivShoppingImage.setOnClickListener {
                findNavController().navigate(R.id.action_addShoppingFragment_to_imagePickFragment)
            }

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    popBackStack()
                }

            }
            requireActivity().onBackPressedDispatcher.addCallback(callback)

        }
    }

    private fun popBackStack() {
        viewModel.obtainedEvent(event = ViewModelEvent.SetCurImageUrl(imageUrl = ""))
        viewModel.obtainedEvent(event = ViewModelEvent.ObtainInitial)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun subscribeToObserver() {
        collectFlow(viewModel.curImageUrl) {
            glide.load(it).into(binding.ivShoppingImage)
        }
        collectFlow(viewModel.insertShoppingItemStatus) { result ->
            when (result) {
                is Resource.Success -> {
                    Snackbar.make(
                        binding.root, "Added shopping item",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.obtainedEvent(event = ViewModelEvent.ObtainInitial)
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        result.msg,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

}