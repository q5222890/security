package com.wen.security.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wen.security.R;
import com.wen.security.utils.SPUtils;
import com.wen.security.utils.ToastUtil;

/**
 *  系统设置
 */

public class SettingsActivity extends BaseActivity {
    private TextView title;
    private EditText url, terminal_id, room_id;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_settings);
        initRes();
        initEvent();

        terminal_id.setText(SPUtils.getTerminalID(this));
        url.setText(SPUtils.getConfigUrl(this));
        room_id.setText(SPUtils.getRoomID(this));
    }

    @Override
    protected void initRes() {
        super.initRes();

        title = (TextView) findViewById(R.id.title);
        url = (EditText) findViewById(R.id.url);
        terminal_id = (EditText) findViewById(R.id.terminal_id);
        room_id = (EditText) findViewById(R.id.room_id);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);

    }

    @Override
    protected void initEvent() {
        super.initEvent();
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){

        switch(v.getId()){
            case R.id.btn_confirm:

                String terminal = terminal_id.getText().toString();
                String room = room_id.getText().toString();
                String base_url = url.getText().toString();
                if(TextUtils.isEmpty(terminal)||TextUtils.isEmpty(room)||TextUtils.isEmpty(base_url)){
                    ToastUtil.showShort("不能为空");
                    return;
                }
                SPUtils.saveConfig(this, base_url, room, terminal);
                break;

            case R.id.ac_top_back:
                back();
                break;


        }
    }

    @Override
    public void back() {
        super.back();
    }
}
