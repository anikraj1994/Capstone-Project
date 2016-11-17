package me.anikraj.campussecrets.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

import me.anikraj.campussecrets.R;


public class Home extends Fragment {
    FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).into((CircularImageView) rootView.findViewById(R.id.dp));
            ((TextView) rootView.findViewById(R.id.name)).setText(auth.getCurrentUser().getDisplayName());
            ((TextView) rootView.findViewById(R.id.email)).setText(auth.getCurrentUser().getEmail());
            Button logout = (Button) rootView.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).logout();
                }
            });
        }
        return rootView;
    }
}
