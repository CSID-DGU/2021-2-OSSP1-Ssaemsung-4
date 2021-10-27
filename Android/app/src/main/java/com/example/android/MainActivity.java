package com.example.android;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton;
    RecordDialog recordDialog;
    String TAG = "aa";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = (ImageButton) findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"here2");
                RecordDialog recordDialog = new RecordDialog(MainActivity.this);

                Log.d(TAG,"here1");
                ImageButton cancelButton = (ImageButton)recordDialog.findViewById(R.id.cancel_button);
                ImageButton recordButton = (ImageButton)recordDialog.findViewById(R.id.record_button);
                ImageButton bookmarkButton = (ImageButton)recordDialog.findViewById(R.id.bookmark_button);
//                cancelButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                });

                recordDialog.getWindow().setGravity(Gravity.BOTTOM);
                recordDialog.show();

            }
        });
    }



}