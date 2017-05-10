package com.wen.security.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wen.security.R;

public class DialogFactory {

    public static Dialog creatLoadDialog(final Activity context) {

        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.ac_load_dlg);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog creatTipDialog(Context context, String title,
            String tip, OnClickListener listener) {

        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.ac_tip_dlg);

        TextView txt_title = (TextView) dialog.findViewById(R.id.title);
        TextView txt_tip = (TextView) dialog.findViewById(R.id.tip);
        Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(listener);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        txt_tip.setText(tip);
        txt_title.setText(title);

        return dialog;
    }
    
    

}
