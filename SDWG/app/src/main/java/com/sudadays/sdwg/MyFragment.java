package com.sudadays.sdwg;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

import static com.sudadays.sdwg.MainActivity.*;

/**
 * Created by 10589 on 2017-2-28-0028.
 */

public class MyFragment extends Fragment {
    int which;
    public MyFragment() {
    }
    @SuppressLint("ValidFragment")
    public MyFragment(int which) {
        this.which=which;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=null;
        switch(which)
        {
            case 1:
                view = inflater.inflate(R.layout.shouyelayout, container, false);
                setview1(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.onetouchlayout, container, false);
                setview2(view);
                break;
            case 3:
                view = inflater.inflate(R.layout.zhanghaolayout, container, false);
                setview3(view);
                break;
        }
        return view;
    }

    void setview1(View view)
    {
        //设置图片
        File f=new File("/sdcard/sudawg/touxiang.png");
        if(f.exists()) {
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_shouye);
            imageView.setImageBitmap(BitmapFactory.decodeFile("/sdcard/sudawg/touxiang.png"));
        }

        if(!id.equals(""))
        {
            TextView tv=(TextView)view.findViewById(R.id.tv_shouye);
            tv.setText(id);
        }
    }

    void setview2(View view)
    {
        //设置图片
        File f=new File("/sdcard/sudawg/sdwg2.png");
        if(f.exists()){
            ImageView imageView=(ImageView)view.findViewById(R.id.iv_onetouch);
            imageView.setImageBitmap(BitmapFactory.decodeFile("/sdcard/sudawg/sdwg2.png"));
        }

        TextView tv=(TextView)view.findViewById(R.id.yijianname);
        tv.setText(yijianname);
    }
    //设置界面状态
    void setview3(View view)
    {
        //设置图片
        File f=new File("/sdcard/sudawg/touxiang.png");
        if(f.exists()) {
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_zhanghao);
            imageView.setImageBitmap(BitmapFactory.decodeFile("/sdcard/sudawg/touxiang.png"));
        }

        EditText et1=(EditText) view.findViewById(R.id.et_zhanghao);
        EditText et2=(EditText) view.findViewById(R.id.et_mima);
        Switch s1=(Switch) view.findViewById(R.id.s1);

        et1.setText(id.toCharArray(),0,id.length());
        et2.setText(pw.toCharArray(),0,pw.length());
        if(m.equals("1"))
            s1.setChecked(true);
        else
            s1.setChecked(false);
    }
}
