package com.example.vix_schoters_jerry_berlin.ui.fragment.news.bottomsheet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.vix_schoters_jerry_berlin.R
import com.example.vix_schoters_jerry_berlin.databinding.FragmentNewsBottomSheetBinding
import com.example.vix_schoters_jerry_berlin.ui.fragment.news.NewsFragmentDirections
import com.example.vix_schoters_jerry_berlin.util.Constant
import com.example.vix_schoters_jerry_berlin.viewmodel.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Locale

class NewsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var newsViewModel: NewsViewModel

    private var countryChip = Constant.DEFAULT_COUNTRY
    private var countryChipId = 0
    private var categoryChip = Constant.DEFAULT_CATEGORY
    private var categoryChipId = 0

    private var _binding: FragmentNewsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsViewModel = ViewModelProvider(requireActivity())[NewsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewsBottomSheetBinding.inflate(inflater, container, false)

        newsViewModel.readCountryAndCategoryType.asLiveData().observe(viewLifecycleOwner){ value ->
            countryChip = value.selectedCountry
            categoryChip = value.selectedCategory
            updateChip(value.selectedCountryId, binding.countryChipGroup)
            updateChip(value.selectedCategoryId, binding.categoryChipGroup)
        }

        binding.countryChipGroup.setOnCheckedStateChangeListener{ group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId.first())
            val selectedCountry = chip.text.toString().lowercase(Locale.ROOT)
            countryChip = selectedCountry
            countryChipId = selectedChipId.first()
        }

        binding.categoryChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId.first())
            val selectedCategory = chip.text.toString().lowercase(Locale.ROOT)
            categoryChip = selectedCategory
            categoryChipId = selectedChipId.first()
        }

        binding.applyButton.setOnClickListener {
            newsViewModel.saveCountryAndCategoryType(
                countryChip,
                countryChipId,
                categoryChip,
                categoryChipId
            )
            Log.d("TEST1", "onCreateView: $countryChip $countryChipId $categoryChip $categoryChipId")
            val action = NewsBottomSheetDirections.actionNewsBottomSheetToNewsFragment(true)
            findNavController().navigate(action)

        }

        return binding.root
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if(chipId!=0){
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            }catch (e: Exception){
                Log.d("TAG", "updateChip: ${e.message.toString()}")
            }
        }
    }

}