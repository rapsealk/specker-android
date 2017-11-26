package com.example.sanghyunj.speckerapp.widget;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.activity.ChatActivity;
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

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by GL552 on 2017-11-26.
 */

public class FriendBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener {

    private Context context;
    private static FriendBottomSheetDialog instance;

    private ImageView mUserProfile;
    private TextView mUserName;
    private ImageButton mChatButton;

    private String mFriendId;

    public static FriendBottomSheetDialog getInstance(@NonNull Context context) {
        if (instance == null) instance = new FriendBottomSheetDialog(context);
        return instance;
    }

    private FriendBottomSheetDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        create();
    }

    public void create() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        setContentView(bottomSheetView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };

        mUserProfile = (ImageView) bottomSheetView.findViewById(R.id.userProfile);
        mUserName = (TextView) bottomSheetView.findViewById(R.id.userName);
        mChatButton = (ImageButton) bottomSheetView.findViewById(R.id.chatButton);

        mUserProfile.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mChatButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userName:
            case R.id.userProfile:
                break;
            case R.id.chatButton:
                FirebaseAuth.getInstance().getCurrentUser().getToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String token = task.getResult().getToken();
                                    ArrayList<String> participants = new ArrayList<>();
                                    participants.add(mFriendId);
                                    Api api = Api.retrofit.create(Api.class);
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
    }

    public FriendBottomSheetDialog setUserProfile(String userProfile) {
        Glide.with(context).load(userProfile).into(mUserProfile);
        return this;
    }

    public FriendBottomSheetDialog setUserName(String userName) {
        mUserName.setText(userName);
        return this;
    }

    public FriendBottomSheetDialog setUserId(String userId) {
        mFriendId = userId;
        return this;
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
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("_id", result);
                context.startActivity(intent);
                dismiss();
            }
        }
    }
}
