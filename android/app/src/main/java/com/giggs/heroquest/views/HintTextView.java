package com.giggs.heroquest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.giggs.heroquest.MyApplication;
import com.giggs.heroquest.R;
import com.giggs.heroquest.utils.ApplicationUtils;

public class HintTextView extends TextView implements View.OnClickListener {

    private static final Typeface font = MyApplication.FONTS.text;

    private int mTextHint;

    public HintTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HintTextView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setTypeface(font);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HintTextView);

        mTextHint = a.getResourceId(R.styleable.HintTextView_textHint, 0);

        boolean linkTextColorToDrawable = a.getBoolean(R.styleable.HintTextView_linkTextColorToDrawable, false);
        if (linkTextColorToDrawable) {
            getCompoundDrawables()[0].setColorFilter(getCurrentTextColor(), PorterDuff.Mode.MULTIPLY);
        }

        a.recycle();

        setOnClickListener(this);
    }

    public void setTextHint(int textHint) {
        mTextHint = textHint;
    }

    @Override
    public void onClick(View view) {
        ApplicationUtils.showToast(getContext(), mTextHint, Toast.LENGTH_SHORT);
    }

}
