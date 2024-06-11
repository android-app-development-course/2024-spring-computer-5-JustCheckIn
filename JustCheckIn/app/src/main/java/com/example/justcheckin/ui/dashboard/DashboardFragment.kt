package com.example.justcheckin.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.justcheckin.GlobalVars
import com.example.justcheckin.R
import com.example.justcheckin.adapter.ListSignInRecordAdapter
import com.example.justcheckin.api.Api
import com.example.justcheckin.app.App
import com.example.justcheckin.bean.ResponseSigninRecord
import com.example.justcheckin.bean.SignIn
import com.example.justcheckin.databinding.FragmentDashboardBinding
import com.example.justcheckin.utils.Constant
import com.example.justcheckin.utils.JsonUtils
import com.example.justcheckin.widget.EndLessOnScrollListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var recycler_signin: RecyclerView? = null
    private var refresh_signin: SwipeRefreshLayout? = null

    private var datas: ArrayList<SignIn>? = null
    private var responseSigninRecord: ResponseSigninRecord? = null
    private var adapter: ListSignInRecordAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        val mLayoutManager: LinearLayoutManager = LinearLayoutManager(this.context)
        recycler_signin!!.layoutManager = mLayoutManager
        adapter = ListSignInRecordAdapter(datas, this.context)
        recycler_signin!!.adapter = adapter
        recycler_signin!!.itemAnimator = DefaultItemAnimator()
        recycler_signin!!.addOnScrollListener(object : EndLessOnScrollListener(mLayoutManager) {

            override  fun onLoadMore(currentPage: Int) {
                loadMoreData(currentPage)
            }
        })
    }

    private fun initView() {
        recycler_signin = view?.findViewById(R.id.recycler_signin) as RecyclerView
        refresh_signin = view?.findViewById(R.id.refresh_signin) as SwipeRefreshLayout
        //refresh_signin.setOnRefreshListener(this)
        datas = java.util.ArrayList()
    }

    /*获取网络数据*/
    private fun initData() {
        /*获得当前登录用户的用户名*/
        val user_name = GlobalVars.login_name
        /*构造请求体*/
        val params = HashMap<String?, String?>()
        params["user_name"] = user_name
        params["page_num"] = "1"
        val jsonObject = JSONObject.wrap(params)
        /*获取请假记录*/
        OkGo.post(Api.GET_SIGNINS) //
            .tag(this) //
            .upJson(jsonObject.toString()) //
            .execute<String>(object : StringCallback() {
                override fun onSuccess(s: String, call: Call, response: Response) {
                    /*关闭提示框*/
                    responseSigninRecord = ResponseSigninRecord()
                    responseSigninRecord = JsonUtils.fromJson(s, ResponseSigninRecord::class.java)
                    if (responseSigninRecord!!.status.equals(Constant.SUCCESS)) {
                        datas!!.addAll(responseSigninRecord!!.msg)
                        if (datas == null || datas!!.size <= 0) {
                            Toast.makeText(
                                view?.context,
                                "无签到记录...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            adapter!!.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(
                            view?.context,
                            "系统错误,请稍后再试...",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
            })
    }

    fun loadMoreData(currentPage: Int) {
        /*获得当前登录用户的用户名*/
        val user_name = GlobalVars.login_name
        /*构造请求体*/
        val params = java.util.HashMap<String?, String?>()
        params["user_name"] = user_name
        params["page_num"] = (currentPage + 1).toString() + ""
        val jsonObject = JSONObject.wrap(params)
        /*获取请假记录*/
        OkGo.post(Api.GET_HOLIDAYS) //
            .tag(this) //
            .upJson(jsonObject.toString()) //
            .execute<String>(object : StringCallback() {
                override fun onSuccess(s: String, call: Call, response: Response) {
                    /*关闭提示框*/
                    responseSigninRecord = ResponseSigninRecord()
                    responseSigninRecord = JsonUtils.fromJson(s, ResponseSigninRecord::class.java)
                    if (responseSigninRecord!!.getStatus().equals(Constant.SUCCESS)) {
                        datas!!.addAll(responseSigninRecord!!.getMsg())
                        if (datas == null || datas!!.size <= 0) {
                            Toast.makeText(
                                view?.context,
                                "无签到记录...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            adapter!!.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(
                            view?.context,
                            "系统错误,请稍后再试...",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
            })
    }

    val mHandler = Handler()
    fun onRefresh() {
        Thread {
            try {
                Thread.sleep(500)
                mHandler.post {
                    refresh_signin!!.isRefreshing = false
                    Toast.makeText(
                        view?.context,
                        "刷新成功,没有新数据",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}