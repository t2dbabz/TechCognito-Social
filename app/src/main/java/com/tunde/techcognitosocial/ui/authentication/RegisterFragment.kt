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
import androidx.navigation.fragment.findNavController
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentRegisterBinding
import com.tunde.techcognitosocial.util.FieldValidators.isStringContainNumber
import com.tunde.techcognitosocial.util.FieldValidators.isStringLowerAndUpperCase
import com.tunde.techcognitosocial.util.FieldValidators.isValidEmail
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        binding.createAccountButton.setOnClickListener {
            hasCompletedRegistration()
        }

        viewModel.userRegistrationStatus.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Resource.Loading -> {
                    Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {

                    val fullName = binding.fullNameEditTextField.text.toString().trim()
                    val userName = binding.usernameEditTextField.text.toString().trim()
                    val emailAddress = binding.emailAddressEditTextField.text.toString().trim()
                    val userId = result.data?.user?.uid!!


                    viewModel.createUserInDatabase(userId, fullName, userName, emailAddress)
                    Toast.makeText(requireActivity(), "Registration Successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }

                is  Resource.Error -> {
                    Toast.makeText(requireActivity(), result.message, Toast.LENGTH_SHORT).show()
                }
             }
        }
    }

    private fun hasCompletedRegistration() {
        if (isValidated()) {
            val emailAddress = binding.emailAddressEditTextField.text.toString().trim()
            val password = binding.passwordEditTextField.text.toString().trim()

            viewModel.registerNewUser(emailAddress, password)
        } else {

            Toast.makeText(requireActivity(), "Please Complete into details", Toast.LENGTH_SHORT).show()

        }
    }




    private fun isValidated(): Boolean =
        validateFullName() && validateUsername() && validateEmail() && validatePassword()






    /**
     * applying text watcher on each text field
     */
    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {

                R.id.fullName_editTextField -> {
                    validateFullName()
                }
                R.id.username_editTextField -> {
                    validateUsername()
                }

                R.id.emailAddress_editTextField -> {
                    validateEmail()
                }

                R.id.password_editTextField -> {
                    validatePassword()
                }
            }
        }
    }


    private fun setupListeners() {
        binding.fullNameEditTextField.addTextChangedListener(TextFieldValidation(binding.fullNameEditTextField))
        binding.usernameEditTextField.addTextChangedListener(TextFieldValidation(binding.usernameEditTextField))
        binding.emailAddressEditTextField.addTextChangedListener(TextFieldValidation(binding.emailAddressEditTextField))
        binding.passwordEditTextField.addTextChangedListener(TextFieldValidation(binding.passwordEditTextField))
    }

    /**
     * field must not be empty
     */
    private fun validateFullName(): Boolean {
        if (binding.fullNameEditTextField.text.toString().trim().isEmpty()) {
            binding.fullNameEditTextField.error = "Required Field!"
            binding.fullNameEditTextField.requestFocus()
            return false
        } else {
            binding.fullNameTextField.isErrorEnabled = false
        }
        return true
    }

    /**
     * field must not be empty
     */
    private fun validateUsername(): Boolean {
        if (binding.usernameEditTextField.text.toString().trim().isEmpty()) {
            binding.userNameTextField.error = "Required Field!"
            binding.usernameEditTextField.requestFocus()
            return false
        } else {
            binding.userNameTextField.isErrorEnabled = false
        }
        return true
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
        } else if (!isValidEmail(binding.emailAddressEditTextField.text.toString())) {
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
     * 2) password length must not be less than 6
     * 3) password must contain at least one digit
     * 4) password must contain atleast one upper and one lower case letter
     * 5) password must contain atleast one special character.
     */
    private fun validatePassword(): Boolean {
        if (binding.passwordEditTextField.text.toString().trim().isEmpty()) {
            binding.passwordTextField.error = "Required Field!"
            binding.passwordEditTextField.requestFocus()
            return false
        } else if (binding.passwordEditTextField.text.toString().length < 6) {
            binding.passwordTextField.error = "password can't be less than 6"
            binding.passwordEditTextField.requestFocus()
            return false
        } else if (!isStringContainNumber(binding.passwordEditTextField.text.toString())) {
            binding.passwordTextField.error = "Required at least 1 digit"
            binding.passwordEditTextField.requestFocus()
            return false
        } else if (!isStringLowerAndUpperCase(binding.passwordEditTextField.text.toString())) {
            binding.passwordTextField.error =
                "Password must contain upper and lower case letters"
            binding.passwordEditTextField.requestFocus()
            return false
        }  else {
            binding.passwordTextField.isErrorEnabled = false
        }
        return true
    }


}