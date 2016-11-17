package me.anikraj.campussecrets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.anikraj.campussecrets.models.Note;

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
        if(model.getType().compareTo(context.getString(R.string.type_love))==0){
            viewHolder.card.setBackgroundColor(context.getResources().getColor(R.color.love));
            type = Typeface.createFromAsset(context.getAssets(),context.getString(R.string.love_font));
            viewHolder.tv.setTypeface(type);
        }
        else if(model.getType().compareTo(context.getString(R.string.type_joke))==0){
            viewHolder.card.setBackgroundColor(context.getResources().getColor(R.color.joke));
            type = Typeface.createFromAsset(context.getAssets(),context.getString(R.string.joke_font));
            viewHolder.tv.setTypeface(type);
        }
        else if(model.getType().compareTo(context.getString(R.string.type_hate))==0){
            viewHolder.card.setBackgroundColor(context.getResources().getColor(R.color.hate));
            type = Typeface.createFromAsset(context.getAssets(),context.getString(R.string.hate_font));
            viewHolder.tv.setTypeface(type);
        }
        else if(model.getType().compareTo(context.getString(R.string.type_conf))==0){
            viewHolder.card.setBackgroundColor(context.getResources().getColor(R.color.confession));
            type = Typeface.createFromAsset(context.getAssets(),context.getString(R.string.conf_font));
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
