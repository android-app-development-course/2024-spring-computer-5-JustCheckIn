package com.example.justcheckin.ui.home

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.WIFI_SERVICE
import androidx.navigation.findNavController
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.TimeUtils
import com.example.justcheckin.GlobalVars
import com.example.justcheckin.HolidayActivity
import com.example.justcheckin.LoginActivity
import com.example.justcheckin.MainActivity
import com.example.justcheckin.R
import com.example.justcheckin.api.Api
import com.example.justcheckin.bean.ResponseLogin
import com.example.justcheckin.databinding.FragmentHomeBinding
import com.example.justcheckin.utils.Constant
import com.example.justcheckin.utils.JsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.text.SimpleDateFormat

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    lateinit var str_mac: String
    lateinit var md: MaterialDialog
    lateinit var login: ResponseLogin

    private var btn_StartAm: Button? = null
    private var btn_EndAm: Button? = null
    private var btn_StartPm: Button? = null
    private var btn_EndPm: Button? = null
    private var btn_StartN: Button? = null
    private var btn_EndN: Button? = null
    private var tv_signin: TextView? = null

    private var actionA: Button? = null
    private var actionB: Button? = null
    private var actionC: Button? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
/*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
*/
        return root
    }

    //private val fruitList = ArrayList<Fruit>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        initFruits() // 初始化水果数据
        val recyclerView = view.findViewById<RecyclerView>(R.id.checkin_list_view)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation= LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        val adapter = FruitAdapter(fruitList)
        recyclerView.adapter = adapter
        */
        initView()


        /*第一个悬浮菜单点击事件*/
        actionA!!.setOnClickListener {
            val intent= Intent(activity, HolidayActivity::class.java)
            startActivity(intent)
        }
        //actionB!!.setOnClickListener {
        //    val navController = findNavController(R.id.nav_host_fragment_activity_main)
        //}
        //actionC!!.setOnClickListener { toActivity(SignInRecordActivity::class.java) }

        btn_StartAm!!.setOnClickListener(View.OnClickListener {
            if (!checkLabMac()) {
                return@OnClickListener
            }
            SignIn(Constant.AM_SIGNIN)
        })

        btn_EndAm!!.setOnClickListener(View.OnClickListener {
            if (!checkLabMac()) {
                return@OnClickListener
            }
            SignIn(Constant.AM_SIGNOUT)
        })

        btn_StartPm!!.setOnClickListener(View.OnClickListener {
            if (!checkLabMac()) {
                return@OnClickListener
            }
            SignIn(Constant.PM_SIGNIN)
        })
        btn_EndPm!!.setOnClickListener(View.OnClickListener {
            if (!checkLabMac()) {
                return@OnClickListener
            }
            SignIn(Constant.PM_SIGNOUT)
        })
        btn_StartN!!.setOnClickListener(View.OnClickListener {
            if (!checkLabMac()) {
                return@OnClickListener
            }
            SignIn(Constant.NIGHT_SIGNIN)
        })
        btn_EndN!!.setOnClickListener(View.OnClickListener {
            if (!checkLabMac()) {
                return@OnClickListener
            }
            SignIn(Constant.NIGHT_SIGNOUT)
        })

    }

    /*private fun initFruits() {
        repeat(1) {
            fruitList.add(Fruit(getRandomLengthName("Apple"), R.drawable.apple_pic))
            fruitList.add(Fruit(getRandomLengthName("Banana"), R.drawable.banana_pic))
            fruitList.add(Fruit(getRandomLengthName("Orange"), R.drawable.orange_pic))
            //fruitList.add(Fruit(getRandomLengthName("Watermelon"), R.drawable.watermelon_pic))
            //fruitList.add(Fruit(getRandomLengthName("Pear"), R.drawable.pear_pic))
            //fruitList.add(Fruit(getRandomLengthName("Grape"), R.drawable.grape_pic))
            //fruitList.add(Fruit(getRandomLengthName("Pineapple"), R.drawable.pineapple_pic))
            //fruitList.add(Fruit(getRandomLengthName("Strawberry"), R.drawable.strawberry_pic))
            //fruitList.add(Fruit(getRandomLengthName("Cherry"), R.drawable.cherry_pic))
            //fruitList.add(Fruit(getRandomLengthName("Mango"), R.drawable.mango_pic))
        }
    }

    private fun getRandomLengthName(name: String): String {
        //val length = Random().nextInt(20) + 1
        val length = 2
        val builder = StringBuilder()
        for (i in 0 until length) {
            builder.append(name)
        }
        return builder.toString()
    }

     */

    private fun initView() {
        btn_StartAm = view?.findViewById(R.id.btn_StartAm) as Button
        btn_EndAm = view?.findViewById(R.id.btn_EndAm) as Button
        btn_StartPm = view?.findViewById(R.id.btn_StartPm) as Button
        btn_EndPm = view?.findViewById(R.id.btn_EndPm) as Button
        btn_StartN = view?.findViewById(R.id.btn_StartN) as Button
        btn_EndN = view?.findViewById(R.id.btn_EndN) as Button
        actionA = view?.findViewById(R.id.btn_req_holi) as Button
        actionB = view?.findViewById(R.id.btn_holi_history) as Button
        actionC = view?.findViewById(R.id.btn_checkin_history) as Button
        tv_signin = view?.findViewById(R.id.date_text) as TextView
        val sdf = SimpleDateFormat("yyyy-MM-dd  EEE")
        val str_Date: String = TimeUtils.getNowString(sdf)
        tv_signin!!.setText("签到日期: "+str_Date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun SignIn(type: String?) {
        showWaitDialog()
        /*获取当前时间*/
        val ss = SimpleDateFormat("yyyy-MM-dd HH:mm")
        /*主要是为了拼凑unique_id*/
        val ss2 = SimpleDateFormat("yyyyMMdd")
        val date: String = TimeUtils.getNowString(ss)
        val date2: String = TimeUtils.getNowString(ss2)

        /*获得当前登录用户的用户名*/
        val user_name: String = GlobalVars.login_name
        /*没人每天只有一个id*/
        val unique_id = user_name + date2

        /*构造请求体*/
        val params = HashMap<String?, String?>()
        params["unique_id"] = unique_id
        params["type"] = type
        params["date"] = date
        params["user_name"] = user_name
        val jsonObject = JSONObject.wrap(params)
        /*发送登录请求*/
        OkGo.post(Api.SIGNIN) //
            .tag(this) //
            .upJson(jsonObject.toString()) //
            .execute<String>(object : StringCallback() {
                override fun onSuccess(s: String, call: Call, response: Response) {
                    Log.e(
                        "Mainactivity____>",
                        response.code.toString() + "状态码：" + call.toString()
                    )
                    if (response.code != 200) {
                        /*打开签到成功提示对话框*/
                        signSuccess("后台维护中...")
                        return
                    }
                    login = ResponseLogin()
                    login = JsonUtils.fromJson(s, ResponseLogin::class.java)
                    if (login.getStatus().equals(Constant.SUCCESS)) {
                        /*关闭加载对话框*/
                        md.dismiss()
                        /*打开签到成功提示对话框*/
                        signSuccess("签到成功")
                    } else if (login.getMsg().equals(Constant.ERROR_SYSTEM)) {
                        /*关闭加载对话框*/
                        md.dismiss()
                        /*打开签到成功提示对话框*/
                        signSuccess("系统发生错误，请联系程序开发者")
                    } else if (login.getMsg().equals(Constant.ERROR_ALREADY_SIGNIN)) {
                        /*关闭加载对话框*/
                        md.dismiss()
                        /*打开签到成功提示对话框*/
                        signSuccess("你已经签过了..请勿重复签！")
                    }
                }
            })
    }

    /*页面跳转*/
    fun toActivity(clazz: Class<*>?) {
        val intent = Intent(activity, clazz)
        startActivity(intent)
    }

    /*签到提示对话框*/
    fun signSuccess(msg: String?) {
        val dialog = MaterialDialog.Builder(this.requireContext())
            .title("提示")
            .content(msg!!)
            .positiveText("确定")
            .show()
    }

    /*比对路由器的MAC地址*/
    fun checkLabMac(): Boolean {
        //获取WiFIMac地址
        if (NetworkUtils.isWifiConnected()) {
            val wifi = activity?.applicationContext?.getSystemService(WIFI_SERVICE) as WifiManager
            val info = wifi.connectionInfo
            str_mac = info.bssid
            if (str_mac != Constant.E412_MAC) {
                signSuccess("非允许WIFI,暂时通过！mac:$str_mac")
                return true
            } else {
                return true
            }
        } else {
            signSuccess("WIFI未连接，请先连接WIFI！")
            return false
        }
    }

    /*显示正在加载对话框*/
    fun showWaitDialog() {
        /*显示提示正在签到对话框*/
        md = MaterialDialog.Builder(this.requireContext())
            .title("提示")
            .content("正在签到...")
            .progress(true, 0)
            .show()
    }
}