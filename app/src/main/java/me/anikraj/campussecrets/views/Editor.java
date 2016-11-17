package me.anikraj.campussecrets.views;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import me.anikraj.campussecrets.R;
import me.anikraj.campussecrets.models.Note;


public class Editor extends Fragment {
    EmojiconEditText editText;
    RelativeLayout mRelativeLayout;
    LinearLayout panel;
    ImageView bgimage;
    Button selectbutton;
    EmojIconActions emojIcon;
    ImageButton emojibutton,camerabutton,gallerybutton;
    FloatingActionButton fab;
    String type;
    String imagelink="";boolean imageselected=false;String returnimageurl="error";
    Uri imageUri;
    FirebaseAuth auth;
    DatabaseReference myRef;
    Switch switch1;

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "me.anikraj.campussecrets.fileprovider";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_editor, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("college/1/notes");

       auth = FirebaseAuth.getInstance();

        bgimage=(ImageView)rootView.findViewById(R.id.backgroundimage);
        emojibutton=(ImageButton)rootView.findViewById(R.id.emojibutton);
        camerabutton=(ImageButton)rootView.findViewById(R.id.camerabutton);
        gallerybutton=(ImageButton)rootView.findViewById(R.id.gallerybutton);
        editText=(EmojiconEditText)rootView.findViewById(R.id.notetext);
        mRelativeLayout=(RelativeLayout)rootView.findViewById(R.id.relativelayout);
        panel=(LinearLayout)rootView.findViewById(R.id.linear);
        selectbutton=(Button)rootView.findViewById(R.id.selectnotebutton);
        switch1=(Switch)rootView.findViewById(R.id.switch1);
        fab=(FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageselected)uploadimage(view);
                else makenote(view);
            }
        });
        emojibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               emojIcon.ShowEmojIcon();
            }
        });
        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        gallerybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchGallery();
            }
        });

        String[] topics = new String[]{
                "Love","Hate","Joke","Secret Event"
        };

        selectbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog.setContentView(R.layout.notepickerdialog);

                Button dialogButton = (Button) dialog.findViewById(R.id.love);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        love();
                        if(imageselected) setimagebackgroundtint();
                        dialog.dismiss();
                    }
                });
                Button dialogButton1 = (Button) dialog.findViewById(R.id.joke);
                dialogButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joke();if(imageselected) setimagebackgroundtint();
                        dialog.dismiss();
                    }
                });
                Button dialogButton2 = (Button) dialog.findViewById(R.id.hate);
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hate();if(imageselected) setimagebackgroundtint();
                        dialog.dismiss();
                    }
                });
                Button dialogButton3 = (Button) dialog.findViewById(R.id.conf);
                dialogButton3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conf();if(imageselected) setimagebackgroundtint();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        love();

        emojIcon =new EmojIconActions(getContext(),rootView,editText,emojibutton);
        emojIcon.setIconsIds(R.drawable.ic_keyboard_black_36dp,R.drawable.ic_tag_faces_black_36dp);
        emojIcon.ShowEmojIcon();


        return rootView;
    }
    private void makenote(View view){
        if (auth.getCurrentUser() != null) {
            if(!editText.getText().toString().isEmpty()) {
                Long c = Calendar.getInstance().getTimeInMillis();
                Note newnote = new Note(editText.getText().toString(), type, auth.getCurrentUser().getDisplayName(), auth.getCurrentUser().getEmail(), switch1.isChecked(), c, "", 0, 0);
                String key = myRef.push().getKey();
                myRef.child(key).setValue(newnote);

                reset();
                Snackbar.make(view, "Posted!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else{
                Snackbar.make(view, "Empty Note!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        else{
            Snackbar.make(view, "Not Logged in!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
    private void makenotewithimage(View view,String url){
        if (auth.getCurrentUser() != null) {
            if(!editText.getText().toString().isEmpty()) {
                Long c = Calendar.getInstance().getTimeInMillis();
                Note newnote = new Note(editText.getText().toString(), type, auth.getCurrentUser().getDisplayName(), auth.getCurrentUser().getEmail(), switch1.isChecked(), c, url, 0, 0);
                String key = myRef.push().getKey();
                myRef.child(key).setValue(newnote);

                reset();
                Snackbar.make(view, "Posted!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else{
                Snackbar.make(view, "Empty Note!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        else{
            Snackbar.make(view, "Not Logged in!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
    public void dispatchTakePictureIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);

    }

    private void dispatchGallery() {
        int ab,sb;
        ab=ContextCompat.getColor(getContext(), R.color.love);
        sb=ContextCompat.getColor(getContext(), R.color.lovedark);
        if(type.compareTo("joke")==0){
            ab=ContextCompat.getColor(getContext(), R.color.joke);
            sb=ContextCompat.getColor(getContext(), R.color.jokedark);
        }
        else if(type.compareTo("hate")==0){
            ab=ContextCompat.getColor(getContext(), R.color.hate);
            sb=ContextCompat.getColor(getContext(), R.color.hatedark);
        }
        else if(type.compareTo("conf")==0){
            ab=ContextCompat.getColor(getContext(), R.color.confession);
            sb=ContextCompat.getColor(getContext(), R.color.confessiondark);
        }

        FishBun.with(Editor.this)
                .setActionBarColor(ab, sb)
                .setPickerCount(1)
                .setRequestCode(2)
                .textOnImagesSelectionLimitReached("You can only choose one!")
                .textOnNothingSelected("Nothing Selected")
                .setReachLimitAutomaticClose(true)
                .startAlbum();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            imagelink=compressImage(imageUri.toString());
            imageselected=true;
            Glide.with(this).load(imagelink).centerCrop().into(bgimage);
            setimagebackgroundtint();
        }
        else if (requestCode == 2 && resultCode == getActivity().RESULT_OK) {
            imagelink=compressImage(data.getStringArrayListExtra(Define.INTENT_PATH).get(0));
            imageselected=true;
            Glide.with(this).load(imagelink).centerCrop().into(bgimage);
            setimagebackgroundtint();
        }

    }
    private void love(){
        type="love";
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.love));
        panel.setBackgroundColor(getResources().getColor(R.color.lovedark));
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/love.ttf");
        editText.setTypeface(type);
        editText.setHint("I love...");
    }
    private void joke(){
        type="joke";
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.joke));
        panel.setBackgroundColor(getResources().getColor(R.color.jokedark));
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/love2.ttf");
        editText.setTypeface(type);
        editText.setHint("Knock Knock...");
    }
    private void hate(){
        type="hate";
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.hate));
        panel.setBackgroundColor(getResources().getColor(R.color.hatedark));
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/hate.ttf");
        editText.setTypeface(type);
        editText.setHint("I hate...");
    }
    private void conf(){
        type="conf";
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.confession));
        panel.setBackgroundColor(getResources().getColor(R.color.confessiondark));
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/secret.ttf");
        editText.setTypeface(type);
        editText.setHint("I broke...");
    }
    private void setimagebackgroundtint(){
        if(type.compareTo("love")==0){
            mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.lovealpha));
        }
        else if(type.compareTo("joke")==0){
            mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.jokealpha));
        }
        else if(type.compareTo("hate")==0){
            mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.hatealpha));
        }
        else if(type.compareTo("conf")==0){
            mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.confessionalpha));
        }
    }

    public void reset(){
        imageselected=false;
        editText.setText("");
        if(type.compareTo("love")==0){
            love();
        }
        else if(type.compareTo("joke")==0){
           joke();
        }
        else if(type.compareTo("hate")==0){
            hate();
        }
        else if(type.compareTo("conf")==0){
            conf();
        }

    }


    SweetAlertDialog pDialog;
    public void uploadimage(final View view){

        pDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Uploading Image");
        pDialog.setCancelable(false);
        pDialog.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://campus-secrets.appspot.com");
        StorageReference imagesRef = storageRef.child("notes").child(auth.getCurrentUser().getEmail());

        Uri file = Uri.fromFile(new File(imagelink));

        StorageMetadata   metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        UploadTask  uploadTask = imagesRef.child("images/"+Calendar.getInstance().getTimeInMillis()+".jpg").putFile(file, metadata);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
               // Log.e("upload","Upload is " + progress + "% done");
                pDialog.setTitleText("Uploading Image "+progress+"%");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("upload","Upload is paused");
                pDialog.setTitleText("Uploading paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("upload",exception.getMessage()+"");
                pDialog.dismiss();
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Image upload failed! Try again!")
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                pDialog.dismiss();
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
               // Log.e("upload",downloadUrl.toString()+"");
                returnimageurl=downloadUrl.toString();
                makenotewithimage(view,returnimageurl);
            }
        });
    }
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }
    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "CampusSecrets/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("typesaved", type);
        outState.putString("imagelinksaved", imagelink);
        outState.putBoolean("imageselectedsaved",imageselected);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            type=savedInstanceState.getString("typesaved");
            imageselected=savedInstanceState.getBoolean("imageselectedsaved");
            imagelink=savedInstanceState.getString("imagelinksaved");
            switch (type){
                case "love":love();break;
                case "joke":joke();break;
                case "hate":hate();break;
                case "conf":conf();break;
            }
            if(imageselected) {
                Glide.with(this).load(imagelink).centerCrop().into(bgimage);
                setimagebackgroundtint();
            }
        }
    }

}
