package com.example.myequilator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_description);
        TextView textView = (TextView) findViewById(R.id.credit_text);
        TextView textView2 = (TextView) findViewById(R.id.credit_text2);
        //textView.setText(getString(R.string.credit_text)+"\n"+getString(R.string.credit_text2));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView2.setMovementMethod(LinkMovementMethod.getInstance());


    }

}
