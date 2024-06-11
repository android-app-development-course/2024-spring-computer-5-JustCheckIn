package com.example.justcheckin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.example.justcheckin.api.Api
import com.example.justcheckin.bean.ResponseLogin
import com.example.justcheckin.utils.Constant
import com.example.justcheckin.utils.JsonUtils
import com.example.justcheckin.utils.PreferencesUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var str_username:String;
    lateinit var str_password:String;
    lateinit var md: MaterialDialog;
    lateinit var login: ResponseLogin;
    lateinit var username:EditText;
    lateinit var password:EditText;
    lateinit var toRegister:TextView;
    lateinit var btn_login:Button;
    lateinit var checkBox:CheckBox;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        //注册
        findViewById<TextView>(R.id.register).setOnClickListener(){
            //跳转
            val intent= Intent()
            intent.setClass(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //返回
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setOnClickListener(){
            finish()
        }

        //登录
        btn_login.setOnClickListener{
            checkUser()
        }
    }

    fun initView() {
        btn_login = findViewById(R.id.btn_login)
        username = findViewById(R.id.edit_username)
        password = findViewById(R.id.edit_passwaord)
        toRegister = findViewById(R.id.register)
        checkBox = findViewById(R.id.checkBox)
        //SharedPreference
        val prefs = getPreferences(Context.MODE_PRIVATE)
        val isRemember = prefs.getBoolean("remember_password", false)

        if (isRemember) {
            // 将账号和密码都设置到文本框中
            val save_username = prefs.getString("username", "")
            val save_password = prefs.getString("password", "")
            checkBox.isChecked = true
            username.setText(save_username)
            password.setText(save_password)
            /*如果SP里面保存了用户名密码，就将它取出来*/
            //str_username = save_username!!
            //str_password = save_password!!
        }
    }

    fun checkUser() {
        /*判断网络是否连接*/
        if (!NetworkUtils.isConnected()) {
            Toast.makeText(this@LoginActivity, "网络未连接...", Toast.LENGTH_SHORT).show()
            return
        }
        str_username = username.text.toString()
        str_password = password.text.toString()
        if (StringUtils.isSpace(str_username)) {
            Toast.makeText(this@LoginActivity, "用户名不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        if (StringUtils.isSpace(str_password)) {
            Toast.makeText(this@LoginActivity, "密码不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        /*显示提示正在登录对话框*/
        md = MaterialDialog.Builder(this)
            .title("提示")
            .content("正在登录...")
            .progress(true, 0)
            .show()

        /*构造请求体*/
        val params = HashMap<String?, String?>()
        params["username"] = str_username
        params["password"] = str_password
        val jsonObject = JSONObject.wrap(params)
        /*发送登录请求*/
        OkGo.post(Api.LOGIN) //
            .tag(this) //
            .upJson(jsonObject.toString()) //
            .execute<String>(object : StringCallback() {
                override fun onSuccess(s: String, call: Call, response: Response) {
                    /*关闭提示框*/
                    /* int code = conn.getResponseCode();//返回码200请求成功，如果请求码不是200，则提示服务器出错*/

                    login = ResponseLogin()
                    login = JsonUtils.fromJson(s, ResponseLogin::class.java)
                    md.dismiss()
                    if (login.status.equals(Constant.SUCCESS)) {
                        /*如果勾选了记住密码，且登录成功，就保存用户名密码*/
                        val prefs = getPreferences(Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        if (checkBox.isChecked) {
                            /*记住用户名密码*/
                            editor.putBoolean("remember_password", true)
                            editor.putString("username", str_username)
                            editor.putString("password", str_password)
                        }

                        /*记住用户名密码*/
                        PreferencesUtils.putString(this@LoginActivity, "username", str_username)
                        PreferencesUtils.putString(this@LoginActivity, "password", str_password)
                        GlobalVars.login_name = str_username
                        val intent= Intent()
                        intent.setClass(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@LoginActivity, "登陆成功", Toast.LENGTH_SHORT).show()
                        this@LoginActivity.finish()
                    } else {
                        if (login.msg.equals(Constant.ERROR_SYSTEM)) {
                            Toast.makeText(this@LoginActivity, "系统错误", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                        if (login.msg.equals(Constant.ERROR_USERNAME)) {
                            Toast.makeText(this@LoginActivity, "用户不存在", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                        if (login.msg.equals(Constant.ERROR_PASSWORD)) {
                            Toast.makeText(this@LoginActivity, "密码错误", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                    }
                }
            })
    }
}