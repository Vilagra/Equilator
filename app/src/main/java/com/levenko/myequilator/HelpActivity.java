package com.levenko.myequilator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView tvDictionaryValues = (TextView) findViewById(R.id.dictionary_text1);
        String term = getString(R.string.mp2) + "," + getString(R.string.co) + "," + getString(R.string.bu) + "...";
        String value = getString(R.string.description_of_position);
        collectTermAndValue(tvDictionaryValues, term, value);

        tvDictionaryValues = (TextView) findViewById(R.id.dictionary_text2);
        term = getString(R.string.flop) + "," + getString(R.string.turn) + "," + getString(R.string.river);
        value = getString(R.string.description_of_street);
        collectTermAndValue(tvDictionaryValues,term,value);

        tvDictionaryValues = (TextView) findViewById(R.id.dictionary_text3);
        term = getString(R.string.equity);
        value = getString(R.string.description_equity);
        collectTermAndValue(tvDictionaryValues,term,value);

        tvDictionaryValues = (TextView) findViewById(R.id.dictionary_text4);
        term = getString(R.string.hand);
        value = getString(R.string.description_hand);
        collectTermAndValue(tvDictionaryValues,term,value);

        tvDictionaryValues = (TextView) findViewById(R.id.dictionary_text5);
        term = getString(R.string.range);
        value = getString(R.string.description_range);
        collectTermAndValue(tvDictionaryValues,term,value);

        tvDictionaryValues = (TextView) findViewById(R.id.dictionary_text6);
        term = getString(R.string.hands);
        value = getString(R.string.description_hands);
        collectTermAndValue(tvDictionaryValues,term,value);

        tvDictionaryValues = (TextView) findViewById(R.id.dictionary_text7);
        term = getString(R.string.for6)+","+ getString(R.string.for10);
        value = getString(R.string.description_size_of_table);
        collectTermAndValue(tvDictionaryValues,term,value);
    }

    private void collectTermAndValue(TextView tv, String term, String value) {
        SpannableString span1 = new SpannableString(term);
        span1.setSpan(new StyleSpan(Typeface.BOLD), 0, term.length(), SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(value);
        span2.setSpan(new StyleSpan(Typeface.ITALIC), 0, value.length(), SPAN_INCLUSIVE_INCLUSIVE);

        tv.setText(TextUtils.concat(span1, " - ", span2));


    }

}
