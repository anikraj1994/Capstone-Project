package me.anikraj.campussecrets.views;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import java.util.Calendar;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import me.anikraj.campussecrets.R;

public class OpenNote extends AppCompatActivity {
    private final SpringSystem springSystem = SpringSystem.create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_note);
        getSupportActionBar().hide();

        ((EmojiconTextView)findViewById(R.id.info_text)).setText(getIntent().getStringExtra("text"));
        ((TextView)findViewById(R.id.time)).setText(DateUtils.getRelativeTimeSpanString(getIntent().getLongExtra("time",0), Calendar.getInstance().getTimeInMillis(), 0));
        ((TextView)findViewById(R.id.user)).setText(getIntent().getStringExtra("username"));
        RelativeLayout card=(RelativeLayout)findViewById(R.id.card_view);
        TextView tv=(TextView)findViewById(R.id.info_text);
        ImageView im=(ImageView)findViewById(R.id.backgroundimage);
        final CardView cardview=(CardView)findViewById(R.id.cardview);
        Typeface type;
        if(getIntent().getStringExtra(getString(R.string.type)).compareTo(getString(R.string.type_love))==0){
            card.setBackgroundColor(getResources().getColor(R.color.love));
            type = Typeface.createFromAsset(getAssets(),getString(R.string.love_font));
            tv.setTypeface(type);
        }
        else if(getIntent().getStringExtra(getString(R.string.type)).compareTo(getString(R.string.type_joke))==0){
            card.setBackgroundColor(getResources().getColor(R.color.joke));
            type = Typeface.createFromAsset(getAssets(),getString(R.string.joke_font));
            tv.setTypeface(type);
        }
        else if(getIntent().getStringExtra(getString(R.string.type)).compareTo(getString(R.string.type_hate))==0){
            card.setBackgroundColor(getResources().getColor(R.color.hate));
            type = Typeface.createFromAsset(getAssets(),getString(R.string.hate_font));
            tv.setTypeface(type);
        }
        else if(getIntent().getStringExtra(getString(R.string.type)).compareTo(getString(R.string.type_conf))==0){
            card.setBackgroundColor(getResources().getColor(R.color.confession));
            type = Typeface.createFromAsset(getAssets(),getString(R.string.conf_font));
            tv.setTypeface(type);
        }

        if(!getIntent().getStringExtra(getString(R.string.imgurl)).isEmpty()){
            Glide.with(this).load(getIntent().getStringExtra(getString(R.string.imgurl))).centerCrop().into(im);

            if(getIntent().getStringExtra(getString(R.string.type)).compareTo(getString(R.string.type_love))==0){
                card.setBackgroundColor(getResources().getColor(R.color.lovealpha));
                type = Typeface.createFromAsset(getAssets(),getString(R.string.love_font));
                tv.setTypeface(type);
            }
            else if(getIntent().getStringExtra(getString(R.string.type)).compareTo(getString(R.string.type_joke))==0){
                card.setBackgroundColor(getResources().getColor(R.color.jokealpha));
                type = Typeface.createFromAsset(getAssets(),getString(R.string.joke_font));
                tv.setTypeface(type);
            }
            else if(getIntent().getStringExtra(getString(R.string.type)).compareTo(getString(R.string.type_hate))==0){
                card.setBackgroundColor(getResources().getColor(R.color.hatealpha));
                type = Typeface.createFromAsset(getAssets(),getString(R.string.hate_font));
                tv.setTypeface(type);
            }
            else if(getIntent().getStringExtra(getString(R.string.type)).compareTo(getString(R.string.type_conf))==0){
                card.setBackgroundColor(getResources().getColor(R.color.confessionalpha));
                type = Typeface.createFromAsset(getAssets(),getString(R.string.conf_font));
                tv.setTypeface(type);
            }
        }

        final Spring spring = springSystem.createSpring();

        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                cardview.setScaleX(scale);
                cardview.setScaleY(scale);
            }
        });

        cardview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        spring.setEndValue(0.1);
                        break;
                    case MotionEvent.ACTION_UP:

                    case MotionEvent.ACTION_CANCEL:



                        spring.setEndValue(0);

                        if(event.getAction()==MotionEvent.ACTION_UP)onBackPressed();

                        break;

                }
                return true;
            }
        });


    }

    private void toast(String text) {
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
    }

}
