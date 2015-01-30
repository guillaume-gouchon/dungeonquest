package com.giggs.heroquest.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.giggs.heroquest.R;

public class CustomAlertDialog extends Dialog implements OnClickListener {

    private OnClickListener mOnClickListener;

    public CustomAlertDialog(Context context, int style, String message, android.content.DialogInterface.OnClickListener onClickListener) {
        super(context, style);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.custom_alert_dialog);
        ((TextView) findViewById(R.id.message)).setText(message);
        findViewById(R.id.ok_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
        mOnClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        mOnClickListener.onClick(this, v.getId());
    }

}