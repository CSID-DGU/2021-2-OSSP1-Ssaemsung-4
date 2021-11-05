package com.example.android;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RecordDialog {

    String TAG = "aa";
    ImageButton cancel_button;
    ImageButton record_button;
    ImageButton bookmark_button;
    private Context context;

    Dialog recordDialog;

    public RecordDialog(Context context) {
        this.context = context;
    }

    //record dialog 생성
    public void callFunction() {

        recordDialog = new Dialog(context);


        recordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        recordDialog.setContentView(R.layout.record_dialog);
        recordDialog.getWindow().setGravity(Gravity.BOTTOM);

        recordDialog.show();

        cancel_button = (ImageButton) recordDialog.findViewById(R.id.cancel_button);
        record_button = (ImageButton) recordDialog.findViewById(R.id.record_button);
        bookmark_button = (ImageButton) recordDialog.findViewById(R.id.bookmark_button);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            //recordDialog view 삭제
            @Override
            public void onClick(View view) {
                destroyDialog();
            }
        });
    }
    public void destroyDialog(){
        recordDialog.dismiss();
    }
}