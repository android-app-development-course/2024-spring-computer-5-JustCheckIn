package com.example.justcheckin.ui.account

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.justcheckin.GlobalVars
import com.example.justcheckin.LoginActivity
import com.example.justcheckin.MainActivity
import com.example.justcheckin.R
import com.example.justcheckin.RegisterActivity
import com.example.justcheckin.databinding.FragmentAccountBinding
import com.example.justcheckin.ui.account.AccountViewModel

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val accountViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
/*
        val textView: TextView = binding.textAccount
        accountViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //退出登录
        view.findViewById<Button>(R.id.button_fragment_logout).setOnClickListener(){
            GlobalVars.login_name = ""
            //跳转
            val intent= Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}