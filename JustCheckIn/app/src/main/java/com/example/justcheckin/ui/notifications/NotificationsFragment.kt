package com.example.justcheckin.ui.notifications

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.example.justcheckin.GlobalVars
import com.example.justcheckin.R
import com.example.justcheckin.adapter.ListHolidayRecordAdapter
import com.example.justcheckin.api.Api
import com.example.justcheckin.bean.Holiday
import com.example.justcheckin.bean.ResponseHolidayRecord
import com.example.justcheckin.databinding.FragmentNotificationsBinding
import com.example.justcheckin.utils.Constant
import com.example.justcheckin.utils.JsonUtils
import com.example.justcheckin.widget.EndLessOnScrollListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null


    private var recycler_view: RecyclerView? = null
    private var refresh_view: SwipeRefreshLayout? = null

    private var datas: ArrayList<Holiday>? = null
    private var responseHolidayRecord: ResponseHolidayRecord? = null
    private var adapter: ListHolidayRecordAdapter? = null
    private var md: MaterialDialog? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*
        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()


        md!!.dismiss()
        adapter = ListHolidayRecordAdapter(datas, this.requireContext())
        val mLayoutManager: LinearLayoutManager = LinearLayoutManager(this.requireContext())
        recycler_view!!.layoutManager = mLayoutManager
        recycler_view!!.setAdapter(adapter)
        recycler_view!!.itemAnimator = DefaultItemAnimator()
        recycler_view!!.addOnScrollListener(object : EndLessOnScrollListener(mLayoutManager) {
            override fun onLoadMore(currentPage: Int) {
                loadMoreData(currentPage)
            }
        })
    }


    private fun initView() {
        /*显示提示正在登录对话框*/
        md = MaterialDialog.Builder(this.requireContext())
            .title("提示")
            .content("数据加载中...")
            .progress(true, 0)
            .show()

        recycler_view = view?.findViewById(R.id.recycler_holi) as RecyclerView
        refresh_view = view?.findViewById(R.id.refresh_holi) as SwipeRefreshLayout
        //refresh_view.setOnRefreshListener(this)
        datas = java.util.ArrayList()
    }

    private fun initData() {
        /*获得当前登录用户的用户名*/
        val user_name = GlobalVars.login_name
        /*构造请求体*/
        val params = HashMap<String?, String?>()
        params["user_name"] = user_name
        params["page_num"] = "1"
        val jsonObject = JSONObject.wrap(params)
        /*获取请假记录*/
        OkGo.post(Api.GET_HOLIDAYS) //
            .tag(this) //
            .upJson(jsonObject.toString()) //
            .execute<String>(object : StringCallback() {
                override fun onSuccess(s: String, call: Call, response: Response) {
                    /*关闭提示框*/
                    responseHolidayRecord = ResponseHolidayRecord()
                    responseHolidayRecord = JsonUtils.fromJson(s, ResponseHolidayRecord::class.java)
                    if (responseHolidayRecord!!.status.equals(Constant.SUCCESS)) {
                        datas!!.addAll(responseHolidayRecord!!.msg)
                        if (datas == null || datas!!.size <= 0) {
                            Toast.makeText(
                                view?.context,
                                "无请假记录...",
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
                    responseHolidayRecord = ResponseHolidayRecord()
                    responseHolidayRecord = JsonUtils.fromJson(s, ResponseHolidayRecord::class.java)
                    if (responseHolidayRecord!!.getStatus().equals(Constant.SUCCESS)) {
                        datas!!.addAll(responseHolidayRecord!!.getMsg())
                        if (datas == null || datas!!.size <= 0) {
                            Toast.makeText(
                                view?.context,
                                "无请假记录...",
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

    private val mHandler = Handler()
    fun onRefresh() {
        Thread {
            try {
                Thread.sleep(500)
                mHandler.post {
                    refresh_view!!.setRefreshing(false)
                    Toast.makeText(
                        this.context,
                        "刷新成功，没有新数据...",
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