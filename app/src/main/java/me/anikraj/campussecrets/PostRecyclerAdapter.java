package me.anikraj.campussecrets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;

import java.util.Calendar;
import java.util.List;

import me.anikraj.campussecrets.models.Note;
import me.anikraj.campussecrets.views.OpenNote;

/**
 * Created by anikr on 11/17/2016.
 */
public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.MyViewHolder> {

    List<Note> list;
    Context context;

    public  PostRecyclerAdapter(List<Note> list, Context c){
        this.context=c;
        this.list=list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noteview_recycler_item, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        Note model=list.get(position);
        viewHolder.tv.setText(model.getText());

        Typeface type;
        if(model.getType().compareTo("love")==0){
            viewHolder.card.setBackgroundColor(context.getResources().getColor(R.color.love));
            type = Typeface.createFromAsset(context.getAssets(),"fonts/love.ttf");
            viewHolder.tv.setTypeface(type);
        }
        else if(model.getType().compareTo("joke")==0){
            viewHolder.card.setBackgroundColor(context.getResources().getColor(R.color.joke));
            type = Typeface.createFromAsset(context.getAssets(),"fonts/love2.ttf");
            viewHolder.tv.setTypeface(type);
        }
        else if(model.getType().compareTo("hate")==0){
            viewHolder.card.setBackgroundColor(context.getResources().getColor(R.color.hate));
            type = Typeface.createFromAsset(context.getAssets(),"fonts/hate.ttf");
            viewHolder.tv.setTypeface(type);
        }
        else if(model.getType().compareTo("conf")==0){
            viewHolder.card.setBackgroundColor(context.getResources().getColor(R.color.confession));
            type = Typeface.createFromAsset(context.getAssets(),"fonts/secret.ttf");
            viewHolder.tv.setTypeface(type);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

   public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        RelativeLayout card;
        public MyViewHolder(View v) {
            super(v);
            tv=(TextView)v.findViewById(R.id.info_text);
            card=(RelativeLayout)v.findViewById(R.id.card_view);
        }
    }
}
