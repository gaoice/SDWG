package com.sudadays.sdwg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class YiJianActivity extends Activity {

    Context mContext;
    String id,pw,m,data;//用户变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moveTaskToBack(true);//防止调到后台MainActivity
        mContext=getApplicationContext();
        if(readuser())//读取文件，设置用户变量
        {
            new Thread()
            {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://a.suda.edu.cn/index.php/index/login");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setConnectTimeout(1000);//超时设置
                        OutputStream out = conn.getOutputStream();
                        out.write(data.getBytes());
                        out.flush();
                        out.close();

                        BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer sb = new StringBuffer();
                        String line;
                        while((line=br.readLine())!= null)
                        {
                            sb.append(line);
                        }

                        toastonthread(MainActivity.getinfo(sb.toString()));
                    } catch (IOException e) {// TODO Auto-generated catch block
                        e.printStackTrace();

                        toastonthread("未连接到苏大校园网");
                    }
                }
            }.start();
        }
        else
        {
            Toast.makeText(YiJianActivity.this, "还未设置账号", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    //读用户数据，设置用户变量
    boolean readuser()
    {
        //创建一个SharedPreferences接口对象
        SharedPreferences read = getSharedPreferences("user", Context.MODE_PRIVATE);
        //获取文件中的值
        id= read.getString("id", "");
        if(id=="")
            return false;

        pw= read.getString("pw", "");
        m= read.getString("m", "");
        data=read.getString("data", "");
        return true;
    }
    //线程中提示信息
    void toastonthread(final String text)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(YiJianActivity.this,text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
