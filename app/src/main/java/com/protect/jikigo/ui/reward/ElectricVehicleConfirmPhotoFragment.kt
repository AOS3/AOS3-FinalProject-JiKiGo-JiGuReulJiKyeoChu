package com.protect.jikigo.ui.reward

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.protect.jikigo.R
import com.protect.jikigo.data.model.Confirm
import com.protect.jikigo.databinding.FragmentElectricVehicleConfirmPhotoBinding
import com.protect.jikigo.utils.getUserId
import com.protect.jikigo.ui.viewModel.ElectricConfirmPhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ElectricVehicleConfirmPhotoFragment : Fragment() {
    private var _binding: FragmentElectricVehicleConfirmPhotoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ElectricConfirmPhotoViewModel by viewModels()

    private lateinit var userId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentElectricVehicleConfirmPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    private fun setLayout() {
        observe()
        onClickListener()
        checkData()
        binding.cardElectricConfirm.setBackgroundResource(R.drawable.card_reward_shape)
    }

    // 시작 시 확인할 데이터
    private fun checkData() {
        lifecycleScope.launch {
            userId = requireContext().getUserId() ?: ""
        }
    }

    private fun observe() {
        binding.apply {
            viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
                ivElectricPhotoPhoto.setImageURI(uri)
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
                when(loading) {
                    false -> {
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        binding.layoutElectricPhotoLoading.visibility = View.GONE
                    }
                    true -> {
                        requireActivity().window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                        binding.layoutElectricPhotoLoading.visibility = View.VISIBLE
                    }
                }
            }

            viewModel.downloadUrl.observe(viewLifecycleOwner) { url ->
                val item = Confirm(userId = userId, confirmImage = url, confirmDate = Timestamp.now(), confirmName = "전기 이동수단")
                viewModel.saveConfirmInfo(item)
            }

            viewModel.isDone.observe(viewLifecycleOwner) {
                if(it) {
                    Toast.makeText(requireContext(), "전기 이동수단 인증 완료", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun onClickListener() {
        binding.apply {
            val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                viewModel.setImageUri(uri)
            }

            binding.toolbarElectricPhoto.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            btnElectricPhotoConfirm.setOnClickListener {
                galleryLauncher.launch("image/*")
            }

            toolbarElectricPhoto.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_profile_edit_done -> {
                        viewModel.imageUri.value?.let { uri ->
                            saveConfirm()
                        } ?: Toast.makeText(requireContext(), "이미지를 선택하세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
    }

    private fun saveConfirm() {
        lifecycleScope.launch {
            viewModel.saveConfirmUri()
        }
    }
}