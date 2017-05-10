package com.wen.security.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.wen.security.R;

/**
 * 初始化对话框
 *
 * @author Administrator
 *
 */
public class InitDialog extends Dialog implements
        android.view.View.OnClickListener {
    private Activity context;
    TextView title;
    public InitDialog(Activity context){
        super(context, R.style.dialog);
        this.context = context;
        setContentView(R.layout.ac_load_dlg);

        title = (TextView) findViewById(R.id.title);
        title.setText("正在更新数据……");
        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

    public void setTip(String tip){
        title.setText(tip);
    }

    @Override
    public void onClick(View v){

    }

}
