package me.anikraj.campussecrets.views;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.anikraj.campussecrets.PostDB;
import me.anikraj.campussecrets.PostProvider;
import me.anikraj.campussecrets.PostRecyclerAdapter;
import me.anikraj.campussecrets.R;
import me.anikraj.campussecrets.models.Note;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Search extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
RecyclerView noteslist;

    OkHttpClient client = new OkHttpClient();

    String run() throws IOException {
        Request request = new Request.Builder()
                .url("https://campus-secrets.firebaseio.com/college/1/notes.json?auth=XHnkavc9QazDSuIlLvT09uwkwh8rMXKYM1ERkJzw")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
getSupportActionBar().setTitle("Search");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noteslist=(RecyclerView)findViewById(R.id.recyclernotes);
        noteslist.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        noteslist.setLayoutManager(mLayoutManager);
        //loads the data to recycler
        getLoaderManager().initLoader(0, null, this);
    }


public void del(){
    getContentResolver().delete(PostProvider.CONTENT_URI,null,null);
}

    public void add(String title,String type) {
        ContentValues values = new ContentValues();
        values.put(PostDB.COL_TITLE,title);
        values.put(PostDB.COL_TYPE,type);


        Uri uri = getContentResolver().insert(
                PostProvider.CONTENT_URI, values);

    }

    public void search(View v){
        String q=((EditText)findViewById(R.id.editText)).getText().toString();
        if(q.isEmpty()){
            Toast.makeText(getApplicationContext(),"search empty",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"searching",Toast.LENGTH_SHORT).show();
            del();
            new SearchFB().execute(q);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String URL = "content://me.anikraj.campussecrets.PostProvider/posts";
        Uri students = Uri.parse(URL);
        String[] projection = new String[] {PostDB.COL_TITLE,PostDB.COL_TYPE };
        switch (id) {
            case 0:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        students,        // Table to query
                        projection,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        List<Note> list=new ArrayList<>();

        if (c.moveToFirst()) {
            do{

                list.add(new Note(c.getString(c.getColumnIndex( PostDB.COL_TITLE)),c.getString(c.getColumnIndex( PostDB.COL_TYPE))));
            } while (c.moveToNext());
        }
        noteslist.setAdapter(new PostRecyclerAdapter(list,this));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //search by getting all notes and manualy searching through them using async task and add to content provider
    public class SearchFB extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {                int c=0;

            try {
                String x = run();
                JSONObject jo=new JSONObject(x);
                Iterator k=jo.keys();
                while(k.hasNext()){//Log.e("hs","next");
                    String first =(String) k.next();//Log.e("hs",first);
                    if (jo.getJSONObject(first).getString("text").toLowerCase().contains(params[0].toLowerCase())) {
                        add(jo.getJSONObject(first).getString("text"), jo.getJSONObject(first).getString("type"));c++;
                    }
                }
            }catch (Exception e){
                //handle
                Log.e("Search",e.getMessage());
            }
            return c;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),"search done, "+aVoid+" results",Toast.LENGTH_SHORT).show();
        }
    }
}
