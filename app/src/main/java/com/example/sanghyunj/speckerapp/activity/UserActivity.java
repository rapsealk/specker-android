package com.example.sanghyunj.speckerapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.fragment.ChatFragment;
import com.example.sanghyunj.speckerapp.retrofit.Api;
import com.example.sanghyunj.speckerapp.retrofit.Body.CreateChatroomBody;
import com.example.sanghyunj.speckerapp.retrofit.Response.ResponseWithObjectId;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.POST;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView _userName = (TextView) findViewById(R.id.userName);
        ImageView _profileImage = (ImageView) findViewById(R.id.profileImage);
        // ImageView _backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
        Button mChatButton = (Button) findViewById(R.id.chatButton);

        String userName = getIntent().getStringExtra("userName");
        String profileImage = getIntent().getStringExtra("profileImage");
        String userId = getIntent().getStringExtra("userId");

        _userName.setText(userName);

        DrawableTypeRequest<String> glide = Glide.with(this).load(profileImage);
        glide.bitmapTransform(new CropCircleTransformation(this)).into(_profileImage);
        // glide.bitmapTransform(new BlurTransformation(this)).into(_backgroundImage);

        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // skip already exists
                Api api = Api.retrofit.create(Api.class);
                FirebaseAuth.getInstance().getCurrentUser().getToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String token = task.getResult().getToken();
                                    ArrayList<String> participants = new ArrayList<>();
                                    participants.add(userId);
                                    Call<ResponseWithObjectId> call = api.createChatroom(token, new CreateChatroomBody(participants));
                                    new CreateChatroomTask().execute(call);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
            }
        });
    }

    private class CreateChatroomTask extends AsyncTask<Call, Void, String> {

        @Override
        protected String doInBackground(Call... params) {
            try {
                Call<ResponseWithObjectId> call = params[0];
                Response<ResponseWithObjectId> response = call.execute();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String chatroomId = response.body()._id;
                        return chatroomId;
                    }
                }
                return null;
            }
            catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("_id", result);
                startActivity(intent);
                finish();
            }
        }
    }
}
