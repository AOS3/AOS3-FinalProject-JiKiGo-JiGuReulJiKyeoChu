package com.protect.jikigo.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.protect.jikigo.databinding.FragmentSignUpSecondBinding


class SignUpSecondFragment : Fragment() {
    private var _binding: FragmentSignUpSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
    }

    private fun onClick() {
        binding.toolbarSignUp.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSignUpDone.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigateUp()
        }
    }
}