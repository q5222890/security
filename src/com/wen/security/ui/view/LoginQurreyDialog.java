package com.wen.security.ui.view;

import android.app.Activity;
import android.content.Intent;

import com.wen.security.DataCache;
import com.wen.security.finger.FingerMG;
import com.wen.security.utils.ToastUtil;

public class LoginQurreyDialog extends LoginDialog {

    public LoginQurreyDialog(Activity context, Class<?> cls) {
        super(context, cls);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void didVerify(int id, boolean success) {
        // 验证指纹
        if (success) {
            String user_id = null;
            try {
                user_id = DataCache.getOne().sMenberInfo.User_ID;
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showShort("user_id不存在");
                return;
            }

            if (user_id.equals(id + "")) {

                FingerMG.getOne().OnCancelBtn();
                ac_change_dlg_msg.setText("验证成功,正在进入");
                Intent intent = new Intent(this.context, toCls);
                this.context.startActivity(intent);
                this.dismiss();

            } else {
                ac_change_dlg_msg.setText("验证失败，请管理员重试");
                // FingerMG.getOne().OnIdentifyBtn();
            }

        } else {
            ac_change_dlg_msg.setText("验证失败，请重试");
            // FingerMG.getOne().OnIdentifyBtn();
        }
    }
}
