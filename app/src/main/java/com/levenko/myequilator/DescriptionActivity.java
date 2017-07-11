package com.levenko.myequilator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_description);
        TextView textView = (TextView) findViewById(R.id.credit_text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


    }

}
