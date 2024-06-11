package com.example.justcheckin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.example.justcheckin.api.Api;
import com.example.justcheckin.app.App;
import com.example.justcheckin.bean.ResponseHoliday;
import com.example.justcheckin.utils.Constant;
import com.example.justcheckin.utils.JsonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toolbar;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class HolidayActivity extends AppCompatActivity {
    private TextView startTime;
    private TextView endTime;
    private EditText reason;
    private Button btn_startTime;
    private Button btn_endTime;
    private Button btn_Send;
    private Toolbar holidayToolbar;
    private String str_startTime;
    private String str_endTime;
    private String str_reason;
    private MaterialDialog md;
    private ResponseHoliday responseHoliday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday);
        initView();
        initEvent();

    }
    private void initView(){
        startTime=(TextView) findViewById(R.id.startTime);
        endTime=(TextView) findViewById(R.id.endTime);
        reason=(EditText)findViewById(R.id.reason);
        btn_startTime=(Button)findViewById(R.id.btn_startTime);
        btn_endTime=(Button)findViewById(R.id.btn_endTime);
        btn_Send=(Button)findViewById(R.id.btn_Send);
        holidayToolbar=(Toolbar)this.findViewById(R.id.holidayToolbar);
        holidayToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HolidayActivity.this.finish();/*关闭当前页*/
            }
        });

    }
    private void initEvent(){

        btn_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //弹出日期选择对话框
                Calendar now = Calendar.getInstance();
                DatePickerDialog dialog= DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener(){
                  @Override
                  public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                      String date =year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                      startTime.setText(date);
                      }},now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                dialog.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        btn_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出日期选择对话框
                Calendar now = Calendar.getInstance();
                DatePickerDialog dialog= DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener(){
                                                                          @Override
                                                                          public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                      String date =year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                      endTime.setText(date);
                      }},now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                dialog.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*判断网络是否连接*/
                if(!NetworkUtils.isConnected()){
                    Toast.makeText(HolidayActivity.this,"网络未连接...",Toast.LENGTH_SHORT).show();
                    return;
                }
                str_startTime=startTime.getText().toString();
                str_endTime=endTime.getText().toString();
                str_reason=reason.getText().toString();
                if(StringUtils.isSpace(str_startTime)){
                    Toast.makeText(HolidayActivity.this,"起始时间不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(StringUtils.isSpace(str_endTime)){
                    Toast.makeText(HolidayActivity.this,"结束时间不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }if(StringUtils.isSpace(str_reason)){
                    Toast.makeText(HolidayActivity.this,"请假理由不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                /*显示提示正在登录对话框*/
                md=new MaterialDialog.Builder(HolidayActivity.this)
                        .title("提示")
                        .content("信息正在上传，请稍等...")
                        .progress(true, 0)
                        .show();

                 /*获得当前登录用户的用户名*/
                String user_name= GlobalVars.INSTANCE.getLogin_name();

                /*构造请求体*/
                HashMap<String, String> params = new HashMap<>();
                params.put("begin_time", str_startTime);
                params.put("end_time", str_endTime);
                params.put("reason", str_reason);
                params.put("user_name", user_name);
                JSONObject jsonObject = new JSONObject(params);
                /*发送登录请求*/
                OkGo.post(Api.ADD_HOLIDAY)//
                        .tag(this)//
                        .upJson(jsonObject.toString())//
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                        /*关闭提示框*/
                                responseHoliday=new ResponseHoliday();
                                responseHoliday= JsonUtils.fromJson(s,ResponseHoliday.class);
                                md.dismiss();
                                if(responseHoliday.getStatus().equals(Constant.SUCCESS)){
                                    Toast.makeText(HolidayActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                    HolidayActivity.this.finish();
                                }else{
                                    Toast.makeText(HolidayActivity.this,"系统错误,请稍后再试...",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
            }
        });
    }

    /*页面跳转*/
    public void toActivity(Class<?> clazz){
        Intent intent = new Intent(this,clazz);
        startActivity(intent);
    }

}
