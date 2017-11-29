package com.example.sanghyunj.speckerapp.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.activity.ChatActivity;
import com.example.sanghyunj.speckerapp.adapter.ChatListAdapter;
import com.example.sanghyunj.speckerapp.database.ChatDbHelper;
import com.example.sanghyunj.speckerapp.retrofit.Api;
import com.example.sanghyunj.speckerapp.retrofit.Body.ChatroomMetaBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.ChatroomMetaResponse;
import com.example.sanghyunj.speckerapp.retrofit.Body.RemoveChatroomBody;
import com.example.sanghyunj.speckerapp.retrofit.ChatRequest;
import com.example.sanghyunj.speckerapp.retrofit.DefaultResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sanghyunJ on 05/03/2017.
 */
public class ChatFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;

    private ChatDbHelper mDbHelper;

    // FIXME : http://hyesunzzang.tistory.com/28
    // private RecyclerView recyclerView;
    private ListView listView;
    public static ChatListAdapter adapter;  // private
    // private ArrayList<String> array = new ArrayList<>();
    public static ArrayList<ChatroomMetaBody> mChatRooms = new ArrayList<>();    // private
    public static boolean mChatRoomFlag = false;

    private LinearLayoutManager linearLayoutManager;

    public ChatFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mDbHelper = new ChatDbHelper(getContext());

        // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        listView = (ListView) rootView.findViewById(R.id.chatListView);
        // recyclerView = (RecyclerView) rootView.findViewById(R.id.thumbnail_image_views);

        // recyclerView.setHasFixedSize(true);

        adapter = new ChatListAdapter(getActivity(), mChatRooms);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // recyclerView.setLayoutManager(linearLayoutManager);

        // recyclerView.setAdapter(adapter);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                ChatroomMetaBody item = (ChatroomMetaBody) parent.getItemAtPosition(position);
                Log.d("ChatFragment", "_id: " + item._id);
                intent.putExtra("_id", item._id);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ChatroomMetaBody room = (ChatroomMetaBody) parent.getItemAtPosition(position);
                CharSequence[] items = { "삭제", "취소" };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(room._id)
                        .setItems(items, (DialogInterface dialog, int which) -> {
                            switch (which) {
                                case 0: {
                                    Toast.makeText(getContext(), room._id + " 삭제", Toast.LENGTH_SHORT).show();
                                    mFirebaseAuth.getCurrentUser().getToken(true)
                                            .addOnCompleteListener((@NonNull Task<GetTokenResult> task) -> {
                                                if (!task.isSuccessful()) return;
                                                String token = task.getResult().getToken();
                                                Api api = Api.retrofit.create(Api.class);
                                                api.removeChatroomRx(token, new RemoveChatroomBody(room._id))
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeOn(Schedulers.io())
                                                        .subscribe((DefaultResponse defaultResponse) -> {
                                                            if (!defaultResponse.result.equals("ok")) return;
                                                            for (int index = 0; index < mChatRooms.size(); index++) {
                                                                if (adapter.getItem(index)._id.equals(room._id)) {
                                                                    mChatRooms.remove(index);
                                                                    adapter.notifyDataSetChanged();
                                                                    break;
                                                                }
                                                            }
                                                            int _result = mDbHelper.removeChat(room._id);
                                                            Log.d("Remove Chat", "result: " + _result + ", room: " + room._id);
                                                        });
                                            })
                                            .addOnFailureListener((@NonNull Exception e) -> {
                                                e.printStackTrace();
                                            });
                                    break;
                                }
                                case 1: {
                                    Toast.makeText(getContext(), room._id + " 취소", Toast.LENGTH_SHORT).show();
                                }
                            }
                            dialog.dismiss();
                        });
                builder.show();
                return true;
            }
        });

        // listView.setDivider(new ColorDrawable(Color.rgb(127, 127, 127)));
        if (mChatRoomFlag == false) {
            mChatRoomFlag = true;
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseAuth.getCurrentUser().getToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String token = task.getResult().getToken();
                                Log.d("Firebase Token", "token: " + token);
                                ChatRequest chatRequest = ChatRequest.retrofit.create(ChatRequest.class);
                                final Call<ChatroomMetaResponse> call = chatRequest.getChatrooms(token);
                            /*
                            chatRequest.getChatrooms(token)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<ChatroomMetaResponse>() {
                                        @Override
                                        public void accept(ChatroomMetaResponse chatroomMetaResponse) throws Exception {
                                            ChatroomMetaBody[] metaBody = chatroomMetaResponse.chatrooms;
                                            for (ChatroomMetaBody data: metaBody) {
                                                array.add(data._id + " (" + data.participants + ")");
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                            */
                                new ChatRetriever().execute(call);

                            } else {
                                Toast.makeText(getActivity(), "채팅방을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "인증에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        /*
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                // return false;
                Log.d("TouchItem", "onInterceptTouchEvent");
                return true;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.d("TouchItem", "onTouchEvent");
                // startActivity(new Intent(getContext(), ChatActivity.class));
                try {
                    startActivity(new Intent(getActivity(), ChatActivity.class));
                }
                catch (Exception exception) {
                    Log.d("Error", exception.getMessage());
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                Log.d("TouchItem", "onRequestDisallowInterceptTouchEvent");
            }
        });
        */

        // recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation()));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private class ChatRetriever extends AsyncTask<Call, Void, String> {

        @Override
        protected String doInBackground(Call... params) {
            try {
                Call<ChatroomMetaResponse> call = params[0];
                Response<ChatroomMetaResponse> response = call.execute();

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ChatroomMetaBody[] chatrooms = response.body().chatrooms;
                        for (ChatroomMetaBody chatroom: chatrooms) {
                            mChatRooms.add(chatroom);
                            // array.add(chatroom._id + " (" + chatroom.participants + ")");
                        }
                        // if (response.body().data.size() == 0) nextIndex = "-1";
                        // else nextIndex = response.body().nextIndex;
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
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }
}


