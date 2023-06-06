package com.example.scumapps.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.scumapps.R
import com.example.scumapps.databinding.FragmentLoginBinding
import com.example.scumapps.ui.HomeActivity
import com.example.scumapps.utils.UtilsContext.dpToPx
import com.example.scumapps.utils.UtilsContext.getScreenWidth
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            val intentAutoLogin = Intent(activity, HomeActivity::class.java)
            startActivity(intentAutoLogin)
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        playAnimation()
    }

    private fun setupView() {
        binding.apply {
            val editTextEmail = edtInputEmailLogin.editText
            val editTextPassword = edtInputPasswordLogin.editText

            editTextEmail?.id = R.id.edt_login_email
            editTextPassword?.id = R.id.edt_login_password

            editTextEmail?.doAfterTextChanged {
                buttonStatus()
            }

            editTextPassword?.doAfterTextChanged {
                buttonStatus()
            }

            btnLogin.setOnClickListener {
                val email: String = editTextEmail?.text.toString()
                val password: String = editTextPassword?.text.toString()

                showLoading(true)
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(requireActivity()) {
                        showLoading(false)
                        val intent = Intent(activity, HomeActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        requireActivity().finish()
                        Toast.makeText(activity, "Login Success", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{error ->
                        showLoading(false)
                        Toast.makeText(
                            activity,
                            error.localizedMessage,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

                //Toast.makeText(activity,"LOGIN",Toast.LENGTH_SHORT).show()
                //val email = editTextEmail?.text
                //val password = editTextPassword?.text

                //val user = User(
                //    email = email.toString(), password = password.toString()
                //)
                //loginViewModel.login(user)
            }

            btnToRegister.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        }
        else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun buttonStatus() {
        binding.apply {
            val isEmailError =
                edtInputEmailLogin.editText?.text.isNullOrEmpty() || edtInputEmailLogin.error != null
            val isPasswordError =
                edtInputPasswordLogin.editText?.text.isNullOrEmpty() || edtInputPasswordLogin.error != null
            btnLogin.isEnabled = !(isEmailError || isPasswordError)
        }
    }

    private fun playAnimation() {
        val width = requireActivity().dpToPx(requireActivity().getScreenWidth())

        ObjectAnimator.ofFloat(binding.ivApp, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding.edtInputEmailLogin, View.ALPHA, 1f).setDuration(700)
        val password = ObjectAnimator.ofFloat(binding.edtInputPasswordLogin, View.ALPHA, 1f).setDuration(700)
        val buttonLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(700)

        val textRegisterFade = ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(0)
        val textRegisterY = ObjectAnimator.ofFloat(binding.tvToRegister, View.TRANSLATION_Y, requireActivity().dpToPx(40f), 0f).apply {
            duration = 1000
        }
        val buttonRegisterFade = ObjectAnimator.ofFloat(binding.btnToRegister, View.ALPHA, 1f).setDuration(0)
        val buttonRegisterY = ObjectAnimator.ofFloat(binding.btnToRegister, View.TRANSLATION_Y, requireActivity().dpToPx(40f), 0f).apply {
            duration = 1000
        }

        val togetherRegister = AnimatorSet().apply {
            playTogether(
                email,
                password,
                buttonLogin,
                textRegisterFade,
                textRegisterY,
                buttonRegisterFade,
                buttonRegisterY
            )
        }
        AnimatorSet().apply {
            playSequentially(togetherRegister)
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}