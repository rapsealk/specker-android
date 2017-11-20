package com.example.sanghyunj.speckerapp.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.adapter.SearchFriendListAdapter;
import com.example.sanghyunj.speckerapp.controller.Firebase;
import com.example.sanghyunj.speckerapp.controller.SearchedListObserver;
import com.example.sanghyunj.speckerapp.model.SearchedList.SearchedListItem;
import com.example.sanghyunj.speckerapp.retrofit.Api;
import com.example.sanghyunj.speckerapp.retrofit.Body.SearchUserBody;
import com.example.sanghyunj.speckerapp.retrofit.Response.SearchUserResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.SearchedUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sanghyunj on 06/06/2017.
 */

public class AddFriendActivity extends Activity implements SearchedListObserver{

    private static String TAG = "AddFriendActivity";

    private String token = null;

    private Button searchFriendBtn;
    private EditText searchFriend;
    private FirebaseDatabase firebaseDatabase;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private RecyclerView searchFriendRecyclerView;
    SearchFriendListAdapter searchFriendListAdapter;
    List<SearchedListItem> searchedListItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        searchedListItemList = new ArrayList<>();

        Firebase.getInstance().addSearchedListObserver(this);
        searchFriendBtn = (Button)findViewById(R.id.search_friend_button);
        searchFriend = (EditText) findViewById(R.id.search_friend);
        searchFriendRecyclerView = (RecyclerView)findViewById(R.id.search_friend_recycler_view);

        searchFriendBtn.setEnabled(false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setAuthToken();

        searchFriendListAdapter = new SearchFriendListAdapter(AddFriendActivity.this);
        searchFriendListAdapter.setFriendItemList(searchedListItemList);

        searchFriendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchFriendRecyclerView.setAdapter(searchFriendListAdapter);

        Api api = Api.retrofit.create(Api.class);

        searchFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), searchFriend.getText(), Toast.LENGTH_LONG).show();
                // Firebase.getInstance().searchedListUpdate(keyword);
                String keyword = searchFriend.getText().toString().trim();
                Call<SearchUserResponse> call = api.searchUser(token, new SearchUserBody(keyword));
                new SearchUserTask().execute(call);
            }
        });
    }

    @Override
    public void update(List<SearchedListItem> searchedListItemList) {
        searchFriendListAdapter.setFriendItemList(searchedListItemList);
        searchFriendListAdapter.notifyDataSetChanged();
    }

    public void setAuthToken() {
        firebaseUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            token = task.getResult().getToken();
                            searchFriendBtn.setEnabled(true);
                        } else setAuthToken();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "토큰을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private class SearchUserTask extends AsyncTask<Call, Void, String> {

        @Override
        protected String doInBackground(Call... params) {
            try {
                Call<SearchUserResponse> call = params[0];
                Response<SearchUserResponse> response = call.execute();

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        searchedListItemList = new ArrayList<>();
                        for (SearchedUser user: response.body().users) {
                            Log.d("SearchedUser", "isKnown: " + user.isKnown + ", name: " + user.getName());
                            searchedListItemList.add(new SearchedListItem(user.isKnown, user));
                        }
                        return response.code() + " " + response.body().toString();
                    }
                    else return response.code() + " response.body() is null";
                }
                return response.code() + " response failed";
            }
            catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("AsyncTask", result);
            // searchFriendListAdapter.notifyDataSetChanged();
            update(searchedListItemList);
        }
    }
}
