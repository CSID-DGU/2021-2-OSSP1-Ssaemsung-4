package com.example.android;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RecordDialog extends Dialog {

    public RecordDialog(@NonNull Context context) {
        super(context);

    }

    public RecordDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RecordDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_dialog);






        //배경 투명
//        getWindow().setBackgroundDrawable(ColorDrawble(Color.TRANSPARENT));

    }
}
