package com.example.justcheckin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.example.justcheckin.bean.ResponseLogin
import com.example.justcheckin.api.Api
import com.example.justcheckin.utils.*
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import org.json.JSONObject

import okhttp3.Call
import okhttp3.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var md: MaterialDialog;
    lateinit var login: ResponseLogin;
    lateinit var str_username:String;
    lateinit var str_password:String;
    lateinit var username_register:EditText;
    lateinit var password_register:EditText;
    lateinit var btn_register:Button;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //返回
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setOnClickListener(){
            finish()
        }
        initView()
        btn_register.setOnClickListener {
            checkUser()
        }
    }

    fun initView(){
        btn_register = findViewById(R.id.btn_register)
        username_register = findViewById(R.id.register_username)
        password_register = findViewById(R.id.register_password)
    }

    fun checkUser(){


        /*判断网络是否连接*/
        if (!NetworkUtils.isConnected()) {
            Toast.makeText(this@RegisterActivity, "网络未连接...", Toast.LENGTH_SHORT).show()
            return
        }
        str_username = username_register.text.toString()
        str_password = password_register.text.toString()
        if (StringUtils.isSpace(str_username)) {
            Toast.makeText(this@RegisterActivity, "用户名不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        if (StringUtils.isSpace(str_password)) {
            Toast.makeText(this@RegisterActivity, "密码不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        /*显示提示正在登录对话框*/
        md = MaterialDialog.Builder(this)
            .title("提示")
            .content("注册中，请稍后...")
            .progress(true, 0)
            .show()


        /*构造请求体*/
        val params = HashMap<String?, String?>()
        params["username"] = str_username
        params["password"] = str_password
        //val jsonObject = JSONObject(params)
        val jsonObject = JSONObject.wrap(params)

        /*发送登录请求*/
        OkGo.post(Api.REGISTER) //
            .tag(this) //
            .upJson(jsonObject.toString()) //
            .execute<String>(object : StringCallback() {
                override fun onSuccess(s: String, call: Call, response: Response) {
                    /*关闭提示框*/
                    login = ResponseLogin()
                    login = JsonUtils.fromJson(s, ResponseLogin::class.java)
                    md.dismiss()
                    if (login.status.equals(Constant.SUCCESS)) {
                        this@RegisterActivity.finish()
                        Toast.makeText(this@RegisterActivity, "注册成功，请登录", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (login.msg.equals(Constant.ERROR_SYSTEM)) {
                            Toast.makeText(this@RegisterActivity, "系统错误", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                        if (login.msg.equals(Constant.ERROR_USER_EXIST)) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "用户名已被注册",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                    }
                }
            })
    }
}