package com.sudadays.sdwg;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    //Fragmen对象
    private MyFragment fshouye,fonetouch,fzhanghao;
    private FragmentManager fManager;

    static String id="",pw="",m="",data="",yijianname="";//用户变量


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fManager = getFragmentManager();

        RadioGroup rg = (RadioGroup) findViewById(R.id.radiogroup);
        rg.setOnCheckedChangeListener(this);

        //检查文件存储路径
        File filepath = new File("/sdcard/sudawg/");
        if(!filepath.exists()) {
            filepath.mkdirs();
        }
        //账号未设置则调到账号设置页面
        RadioButton rb;
        if(readuser())
            rb = (RadioButton) findViewById(R.id.rb_shouye);
        else
            rb = (RadioButton) findViewById(R.id.rb_zhanghao);
        rb.setChecked(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //未选择图片，或选择图片后未裁剪
        if(data==null)
            return;
        switch(requestCode)
        {
            case 0:
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(Uri.parse(data.getData().toString()),"image/*");
                // 设置裁剪
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 256);
                intent.putExtra("outputY", 256);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file:///sdcard/sudawg/sdwg2.png"));
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                startActivityForResult(intent, 2);
                return;
            case 1:
                Intent intent1 = new Intent("com.android.camera.action.CROP");
                intent1.setDataAndType(Uri.parse(data.getData().toString()),"image/*");
                // 设置裁剪
                intent1.putExtra("crop", "true");
                intent1.putExtra("aspectX", 1);
                intent1.putExtra("aspectY", 1);
                intent1.putExtra("outputX", 256);
                intent1.putExtra("outputY", 256);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file:///sdcard/sudawg/touxiang.png"));
                intent1.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                startActivityForResult(intent1, 3);
                return;
            case 2:
                ImageView imageView=(ImageView)findViewById(R.id.iv_onetouch);
                imageView.setImageBitmap(BitmapFactory.decodeFile("/sdcard/sudawg/sdwg2.png"));
                break;
            case 3:
                ImageView imageView1=(ImageView)findViewById(R.id.iv_zhanghao);
                Bitmap btm=BitmapFactory.decodeFile("/sdcard/sudawg/touxiang.png");
                imageView1.setImageBitmap(btm);
                if(fshouye!=null) {
                    ImageView imageView2 = (ImageView) findViewById(R.id.iv_shouye);
                    imageView2.setImageBitmap(btm);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //后台运行
        moveTaskToBack(false);
    }
    //================================================底部导航栏监听函数================================================
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (checkedId) {
            case R.id.rb_shouye:
                if (fshouye == null) {
                    fshouye = new MyFragment(1);
                    fTransaction.add(R.id.frame, fshouye);
                } else {
                    fTransaction.show(fshouye);
                }
                break;
            case R.id.rb_onetouch:
                if (fonetouch == null) {
                    fonetouch = new MyFragment(2);
                    fTransaction.add(R.id.frame, fonetouch);
                } else {
                    fTransaction.show(fonetouch);
                }
                break;
            case R.id.rb_zhanghao:
                if (fzhanghao == null) {
                    fzhanghao = new MyFragment(3);
                    fTransaction.add(R.id.frame, fzhanghao);
                } else {
                    fTransaction.show(fzhanghao);
                }
                break;
        }
        fTransaction.commit();
    }
    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fshouye != null)fragmentTransaction.hide(fshouye);
        if(fonetouch != null)fragmentTransaction.hide(fonetouch);
        if(fzhanghao != null)fragmentTransaction.hide(fzhanghao);
    }
    //=========================================账号页面所用函数=======================================================
    // 保存用户信息
    public void b1Proc (View v)
    {
        EditText et1=(EditText) findViewById(R.id.et_zhanghao);
        EditText et2=(EditText) findViewById(R.id.et_mima);
        Switch s1=(Switch) findViewById(R.id.s1);
        etshijiao(et1,et2);

        id=et1.getText().toString();
        pw=et2.getText().toString();
        if(id.equals("")) {
            Toast.makeText(MainActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pw.equals("")) {
            Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(s1.isChecked())
            m="1";
        else
            m="0";
        data="username=" + id + "&password=" + Base64.encodeToString(pw.getBytes(), Base64.DEFAULT) + "&enablemacauth=" + m;//生成含加密数据的字符串

        //创建一个SharedPreferences.Editor接口对象，user表示要写入的XML文件名，MODE_WORLD_WRITEABLE写操作
        SharedPreferences.Editor editor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        //将值放入user文件
        editor.putString("id", id);
        editor.putString("pw", pw);
        editor.putString("m", m);
        editor.putString("data", data);
        //提交
        editor.commit();

        if(fshouye!=null) {
            TextView tv=(TextView)findViewById(R.id.tv_shouye);
            tv.setText(id);
        }

        Toast.makeText(MainActivity.this, "账号信息 | 保存成功", Toast.LENGTH_SHORT).show();
    }

    // 账号设置页面，点击背景，edittext失去焦点
    public void zhanghao_bg_Proc(View v)
    {
        EditText et1=(EditText) findViewById(R.id.et_zhanghao);
        EditText et2=(EditText) findViewById(R.id.et_mima);
        etshijiao(et1,et2);
    }

    //
    public void zhanghao_ivProc(View v)
    {
        //选取图片
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }
    //=============================================OnrTouch页面所用函数===================================================
    // 创建一键登录快捷方式
    public void b2Proc (View v)
    {
        EditText et=(EditText)findViewById(R.id.yijianname) ;
        etshijiao(et);
        //保存快捷方式的名称
        yijianname=et.getText().toString();
        SharedPreferences.Editor editor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        editor.putString("yijianname", yijianname);
        editor.commit();
        //创建快捷方式的Intent
        Intent shortcut= new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //不允许重复创建//名称 //图标//点击快捷图片，运行的程序主入口
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,yijianname);

        File f=new File("/sdcard/sudawg/sdwg2.png");
        if(f.exists())
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeFile("/sdcard/sudawg/sdwg2.png"));
        else
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.sdwg2));

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext() , YiJianActivity.class));
        //发送广播
        sendBroadcast(shortcut);
        Toast.makeText(MainActivity.this, "快捷方式 | 正在创建", Toast.LENGTH_SHORT).show();
    }
    public void onetouch_ivProc(View v)
    {
        //选取图片
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,0);
    }
    public void onetouch_bg_Proc(View v)
    {
        EditText et=(EditText)findViewById(R.id.yijianname) ;
        etshijiao(et);
    }
    public void onetouch_tvProc(View v)
    {
        //恢复默认图片
        File f=new File("/sdcard/sudawg/sdwg2.png");
        if(f.exists())
            f.delete();

        ImageView imageView=(ImageView)findViewById(R.id.iv_onetouch);
        imageView.setImageResource(R.mipmap.sdwg2);
        //写入默认名称
        yijianname="一键登录";
        SharedPreferences.Editor editor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        editor.putString("yijianname", yijianname);
        editor.commit();
        //设置默认名称
        TextView tv=(TextView)findViewById(R.id.yijianname);
        tv.setText(yijianname);
    }
    //=========================================首页页面所用函数=======================================================
    // 登录
    public void b3Proc (View v)
    {
        if(!id.equals(""))//读取文件，设置用户变量
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

                        toastonthread(getinfo(sb.toString()));
                    } catch (IOException e) {// TODO Auto-generated catch block
                        e.printStackTrace();

                        toastonthread("未连接到苏大校园网");
                    }
                }
            }.start();
        }
        else
        {
            Toast.makeText(MainActivity.this, "未设置账号", Toast.LENGTH_SHORT).show();
        }
    }
    //注销
    public void b4Proc (View v)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try {
                    URL url=new URL("http://a.suda.edu.cn/index.php/index/logout");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setConnectTimeout(1000);//超时设置

                    BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while((line=br.readLine())!= null)
                    {
                        sb.append(line);
                    }

                    toastonthread(getinfo(sb.toString()));
                } catch (IOException e) {// TODO Auto-generated catch block
                    e.printStackTrace();

                    toastonthread("未连接到苏大校园网");
                }
            }
        }.start();
    }
    //================================================通用函数定义================================================
    // 读用户数据，设置用户变量
    boolean readuser()
    {
        //创建一个SharedPreferences接口对象
        SharedPreferences read = getSharedPreferences("user", Context.MODE_PRIVATE);
        //获取文件中的值
        yijianname=read.getString("yijianname", "一键登录");
        id= read.getString("id", "");
        if(id.equals(""))
            return false;

        pw= read.getString("pw", "");
        m= read.getString("m", "");
        data=read.getString("data", "");

        return true;
    }
    //使edittext失去焦点
    void etshijiao(EditText et1,EditText et2)
    {
        if(et1.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et1.getWindowToken(), 0);
            et1.clearFocus();//使editText1失去焦点
        }
        if(et2.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et2.getWindowToken(), 0);
            et2.clearFocus();//使editText2失去焦点
        }
    }
    void etshijiao(EditText et1)
    {
        if(et1.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et1.getWindowToken(), 0);
            et1.clearFocus();//使editText1失去焦点
        }
    }
    //线程中提示信息
    void toastonthread(final String text)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //从网页返回值中，提取并转换有用信息
    static String getinfo(String data)
    {
        Pattern pattern = Pattern.compile("(\"info\":\")(.*?)(\")");
        Matcher matcher = pattern.matcher(data);
        String info;
        if(matcher.find())
            info=matcher.group(2);
        else
            return "网关未返回信息";

        StringBuffer sb = new StringBuffer();
        String[] chararray = info.split("\\\\u");
        for (int i = 1; i < chararray.length; i++)
        {
            if(chararray[i].length()>4)
            {
                sb.append((char) Integer.parseInt(chararray[i].substring(0, 4),16));
                sb.append(chararray[i].substring(4, chararray[i].length()));
            }
            else
            {
                sb.append((char) Integer.parseInt(chararray[i],16));
            }
        }
        return sb.toString();
    }
}
