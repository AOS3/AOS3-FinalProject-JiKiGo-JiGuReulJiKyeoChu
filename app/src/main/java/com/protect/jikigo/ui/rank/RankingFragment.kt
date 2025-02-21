package com.protect.jikigo.ui.rank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.protect.jikigo.databinding.FragmentRankingBinding
import com.protect.jikigo.ui.rank.dialog.RankingHelpDialog
import androidx.navigation.fragment.findNavController
import com.protect.jikigo.R
import com.protect.jikigo.ui.extensions.statusBarColor
import androidx.recyclerview.widget.LinearLayoutManager
import com.protect.jikigo.data.Storage
import com.protect.jikigo.ui.adapter.RankingAdapter
import com.protect.jikigo.ui.viewModel.RankingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private val rankingViewModel: RankingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 도움말 아이콘 클릭 시 다이얼로그 표시
        setLayout()
        observeRankingData()
    }


    private fun setLayout() {
        setStatusBarColor()
        onClickToolbar()
        onClickHelp()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvRanking.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeRankingData() {
        rankingViewModel.rankingList.observe(viewLifecycleOwner, Observer { rankingList ->
            binding.rvRanking.adapter = RankingAdapter(rankingList)
        })
    }

    private fun onClickHelp() {
        binding.ivRankingHelp.setOnClickListener {
            val dialog = RankingHelpDialog()
            dialog.show(childFragmentManager, "RankingHelpDialog")
        }
    }

    private fun setStatusBarColor() {
        requireActivity().statusBarColor(R.color.primary)
    }

    private fun onClickToolbar() {
        binding.toolbarRanking.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}