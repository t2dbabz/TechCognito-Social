package com.tunde.techcognitosocial.ui.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentLoginBinding
import com.tunde.techcognitosocial.util.FieldValidators
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        binding.textViewSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.createAccountButton.setOnClickListener {
            confirmLoginDetails()
        }

        viewModel.userSignInStatus.observe(viewLifecycleOwner) { result ->

            when(result) {
                is Resource.Loading -> {
                    binding.loginProgressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.loginProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireActivity(), "Login Successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                    requireActivity().finish()
                }

                is  Resource.Error -> {
                    binding.loginProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireActivity(), result.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setupListeners() {
        binding.emailAddressEditTextField.addTextChangedListener(TextFieldValidation(binding.emailAddressEditTextField))
        binding.passwordEditTextField.addTextChangedListener(TextFieldValidation(binding.passwordEditTextField))
    }


    private fun confirmLoginDetails() {
        if (isValidated()) {
            val emailAddress = binding.emailAddressEditTextField.text.toString().trim()
            val password = binding.passwordEditTextField.text.toString().trim()
            viewModel.signInUser(emailAddress, password)
        } else {
            Toast.makeText(requireActivity(), "Incorrect Email or Password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidated(): Boolean =
        validateEmail() && validatePassword()
    /**
     * applying text watcher on each text field
     */
    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {

                R.id.emailAddress_editTextField -> {
                    validateEmail()
                }

                R.id.password_editTextField -> {
                    validatePassword()
                }
            }
        }
    }


    /**
     * 1) field must not be empty
     * 2) text should matches email address format
     */
    private fun validateEmail(): Boolean {
        if (binding.emailAddressEditTextField.text.toString().trim().isEmpty()) {
            binding.emailAddressEditTextField.error = "Required Field!"
            binding.emailAddressEditTextField.requestFocus()
            return false
        } else if (!FieldValidators.isValidEmail(binding.emailAddressEditTextField.text.toString())) {
            binding.emailAddressTextField.error = "Invalid Email!"
            binding.emailAddressEditTextField.requestFocus()
            return false
        } else {
            binding.emailAddressTextField.isErrorEnabled = false
        }
        return true
    }

    /**
     * 1) field must not be empty
     */
    private fun validatePassword(): Boolean {
        if (binding.passwordEditTextField.text.toString().trim().isEmpty()) {
            binding.passwordTextField.error = "Required Field!"
            binding.passwordEditTextField.requestFocus()
            return false
        }  else {
            binding.passwordTextField.isErrorEnabled = false
        }
        return true
    }


}