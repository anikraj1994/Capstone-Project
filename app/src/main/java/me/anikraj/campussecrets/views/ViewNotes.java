package me.anikraj.campussecrets.views;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

import me.anikraj.campussecrets.R;
import me.anikraj.campussecrets.models.Note;

public class ViewNotes extends Fragment {
RecyclerView noteslist;
    LinearLayoutManager mLayoutManager;
    //   SmallBang mSmallBang;ImageView mImage;

    private final SpringSystem springSystem = SpringSystem.create();

    DatabaseReference myRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_view_notes, container, false);
        ((ImageView)rootView.findViewById(R.id.searchbutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),Search.class));

            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("college/1/notes");

        Toolbar toolbar  = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Posts");
       //  mSmallBang = SmallBang.attach2Window(getActivity());


        noteslist=(RecyclerView)rootView.findViewById(R.id.recyclernotes);
        noteslist.setHasFixedSize(true);
           mLayoutManager= new LinearLayoutManager(getContext());
           mLayoutManager.setReverseLayout(true);
           mLayoutManager.setStackFromEnd(true);
           noteslist.setLayoutManager(mLayoutManager);


        Query queryRef = myRef.orderByChild("ordernewtime");
        FirebaseRecyclerAdapter<Note, NoteViewHolder> adaptor = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                Note.class,
                R.layout.noteview_recycler_item,
                NoteViewHolder.class,
                myRef

        ) {



            @Override
            protected void populateViewHolder(final NoteViewHolder viewHolder, final Note model, int position) {
                viewHolder.time.setText(DateUtils.getRelativeTimeSpanString(model.getTime(), Calendar.getInstance().getTimeInMillis(), 0));
                viewHolder.tv.setText(model.getText());
                if(!model.getAnonymous())viewHolder.user.setText(model.getUsername());

                final Spring spring = springSystem.createSpring();
                spring.addListener(new SimpleSpringListener() {

                    @Override
                    public void onSpringUpdate(Spring spring) {
                        float value = (float) spring.getCurrentValue();
                        float scale = 1f - (value * 0.5f);
                        viewHolder.cardview.setScaleX(scale);
                        viewHolder.cardview.setScaleY(scale);
                    }
                });

                viewHolder.cardview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                spring.setEndValue(0.1);
                                break;
                            case MotionEvent.ACTION_UP:
                                Log.e("motion","button up");
                            case MotionEvent.ACTION_CANCEL:
                                Log.e("motion","button canc");

                                spring.setEndValue(0);
                                if(MotionEvent.ACTION_UP==event.getAction()){
                                    Intent intent =new Intent(getContext(),OpenNote.class);
                                    String transitionName1 = getString(R.string.transition_note_text);
                                    String transitionName3 = getString(R.string.transition_card);
                                    Pair<View, String> p1 = Pair.create((View)viewHolder.tv, transitionName1);
                                    Pair<View, String> p3 = Pair.create((View)viewHolder.cardview, transitionName3);
                                    ActivityOptionsCompat options =
                                            ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),p3);


                                    intent.putExtra("text",model.getText());
                                    intent.putExtra("anon",model.getAnonymous());
                                    intent.putExtra("comments",model.getComments());
                                    intent.putExtra("type",model.getType());
                                    intent.putExtra("username",model.getUsername());
                                    intent.putExtra("useremail",model.getUseremail());
                                    intent.putExtra("time",model.getTime());
                                    intent.putExtra("imgurl",model.getImageurl());
                                    intent.putExtra("likes",model.getLikes());
                                    ActivityCompat.startActivity(getActivity(),intent, options.toBundle());
                                }

                                break;
                            case MotionEvent.ACTION_BUTTON_PRESS:
                                Log.e("motion","button pree");
                                break;
                            case MotionEvent.ACTION_BUTTON_RELEASE:
                                Log.e("motion","button release");
                                break;
                        }
                        return true;
                    }
                });


                Typeface type;
                if(model.getType().compareTo("love")==0){
                    viewHolder.card.setBackgroundColor(getResources().getColor(R.color.love));
                     type = Typeface.createFromAsset(getContext().getAssets(),"fonts/love.ttf");
                    viewHolder.tv.setTypeface(type);
                }
                else if(model.getType().compareTo("joke")==0){
                    viewHolder.card.setBackgroundColor(getResources().getColor(R.color.joke));
                    type = Typeface.createFromAsset(getContext().getAssets(),"fonts/love2.ttf");
                    viewHolder.tv.setTypeface(type);
                }
                else if(model.getType().compareTo("hate")==0){
                    viewHolder.card.setBackgroundColor(getResources().getColor(R.color.hate));
                    type = Typeface.createFromAsset(getContext().getAssets(),"fonts/hate.ttf");
                    viewHolder.tv.setTypeface(type);
                }
                else if(model.getType().compareTo("conf")==0){
                    viewHolder.card.setBackgroundColor(getResources().getColor(R.color.confession));
                    type = Typeface.createFromAsset(getContext().getAssets(),"fonts/secret.ttf");
                    viewHolder.tv.setTypeface(type);
                }

                if(!model.getImageurl().isEmpty()){
                    Glide.with(getContext()).load(model.getImageurl()).centerCrop().into(viewHolder.im);

                    if(model.getType().compareTo("love")==0){
                        viewHolder.card.setBackgroundColor(getResources().getColor(R.color.lovealpha));
                        type = Typeface.createFromAsset(getContext().getAssets(),"fonts/love.ttf");
                        viewHolder.tv.setTypeface(type);
                    }
                    else if(model.getType().compareTo("joke")==0){
                        viewHolder.card.setBackgroundColor(getResources().getColor(R.color.jokealpha));
                        type = Typeface.createFromAsset(getContext().getAssets(),"fonts/love2.ttf");
                        viewHolder.tv.setTypeface(type);
                    }
                    else if(model.getType().compareTo("hate")==0){
                        viewHolder.card.setBackgroundColor(getResources().getColor(R.color.hatealpha));
                        type = Typeface.createFromAsset(getContext().getAssets(),"fonts/hate.ttf");
                        viewHolder.tv.setTypeface(type);
                    }
                    else if(model.getType().compareTo("conf")==0){
                        viewHolder.card.setBackgroundColor(getResources().getColor(R.color.confessionalpha));
                        type = Typeface.createFromAsset(getContext().getAssets(),"fonts/secret.ttf");
                        viewHolder.tv.setTypeface(type);
                    }
                }

            }

        };
        noteslist.setAdapter(adaptor);
        return rootView;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tv,time,user;
        RelativeLayout card;
        ImageView im,fav;
        View cardview;
        public NoteViewHolder(View v) {
            super(v);
            cardview=v.findViewById(R.id.cardview);
            tv=(TextView)v.findViewById(R.id.info_text);
            time=(TextView)v.findViewById(R.id.time);
            card=(RelativeLayout)v.findViewById(R.id.card_view);
            im=(ImageView)v.findViewById(R.id.backgroundimage);
           // fav= (ImageView)v.findViewById(R.id.fav);
            user=(TextView)v.findViewById(R.id.user);
        }

    }

    private void toast(String text) {
        Toast.makeText(getContext(),text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("someVarB", noteslist.getLayoutManager().onSaveInstanceState());
   }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(savedInstanceState != null)
                {
                    Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("someVarB");
                    noteslist.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                }
            }
        }, 100);

    }

}
