package com.example.shoppingapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.adapters.ImageAdapter
import com.example.shoppingapp.databinding.FragmentImagePickBinding
import com.example.shoppingapp.others.Constants.Companion.GRID_SPAN_COUNT
import com.example.shoppingapp.others.Constants.Companion.SEARCH_IMAGES_DELAY
import com.example.shoppingapp.others.Resource
import com.example.shoppingapp.others.collectFlow
import com.example.shoppingapp.ui.events.ViewModelEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImagePickFragment @Inject constructor(
    val imageAdapter: ImageAdapter
) : Fragment() {

    private var _binding: FragmentImagePickBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: ShoppingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImagePickBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ShoppingViewModel::class.java]
        subscribesToObserver()
        setupAdapter()
        imageAdapter.setOnItemClickListener {
            findNavController().popBackStack()
            viewModel.obtainedEvent(event = ViewModelEvent.SetCurImageUrl(imageUrl = it))
            viewModel.obtainedEvent(event = ViewModelEvent.ObtainInitial)
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_IMAGES_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.obtainedEvent(event = ViewModelEvent.SearchImages(imageQuery = it.toString()))
                    }
                }
            }
        }


    }

    private fun subscribesToObserver() {
        collectFlow(viewModel.images) { imageResponse ->
            when (imageResponse) {
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        "Error: ${imageResponse.msg}", Snackbar.LENGTH_LONG
                    )
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
                is Resource.Success -> {
                    val images = imageResponse.data.imageResults
                    val urls = images.map { imageResult ->
                        imageResult.previewURL
                    }

                    imageAdapter.images = urls
                    binding.progressBar.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Initial -> {
                    imageAdapter.images = emptyList()
                }

            }
        }
    }


    private fun setupAdapter() {
        with(binding.rvImages) {
            adapter = imageAdapter
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}