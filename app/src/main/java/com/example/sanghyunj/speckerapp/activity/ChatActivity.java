package com.example.sanghyunj.speckerapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.database.ChatDbHelper;
import com.example.sanghyunj.speckerapp.database.FriendDbHelper;
import com.example.sanghyunj.speckerapp.fragment.ChatFragment;
import com.example.sanghyunj.speckerapp.model.ChatMessage;

import com.example.sanghyunj.speckerapp.retrofit.Body.ChatroomMetaBody;
import com.example.sanghyunj.speckerapp.retrofit.ChatLog;
import com.example.sanghyunj.speckerapp.retrofit.ChatRequest;
import com.example.sanghyunj.speckerapp.retrofit.GetChatBody;
import com.example.sanghyunj.speckerapp.retrofit.GetChatResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.Friend;
import com.example.sanghyunj.speckerapp.util.GlobalVariable;
import com.example.sanghyunj.speckerapp.util.SharedPreferenceManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lineplus on 2017. 5. 16..
 */

public class ChatActivity extends Activity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;

        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) v.findViewById(R.id.messageTextView);  // itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) v.findViewById(R.id.messageImageView);   // itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) v.findViewById(R.id.messengerTextView);  // itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) v.findViewById(R.id.messengerImageView); // itemView.findViewById(R.id.messengerImageView);
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {

        private ArrayList<ChatMessage> messages;
        private static final int MY_MESSAGE = 0;
        private static final int NOT_MY_MESSAGE = 1;

        public RecyclerAdapter(ArrayList<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout = (viewType == MY_MESSAGE) ? R.layout.item_my_message : R.layout.item_message;
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            view.findViewById(R.id.messageTextView).setBackgroundResource((viewType == MY_MESSAGE) ? R.drawable.chat_right : R.drawable.chat_left);
            MessageViewHolder viewHolder = new MessageViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MessageViewHolder viewHolder, int position) {
            ChatMessage message = messages.get(position);
            viewHolder.messageTextView.setText(message.getText());
            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
            // viewHolder.messengerTextView.setText(messages.get(position).getName());
            // viewHolder.messengerTextView.setVisibility(TextView.VISIBLE);
            // viewHolder.messengerImageView.setVisibility(ImageView.GONE);

            if (!message.getName().equals(mSender)) {
                String fUid = messages.get(position).getName();
                Log.d("fUid", fUid);
                Friend sender = mFriendDbHelper.getFriendById(fUid);
                if (sender != null) {
                    Log.d("sender", sender.toString());
                    viewHolder.messengerTextView.setText(sender.name);
                    String profile = sender.getProfile();
                    if (profile != null) {
                        Glide.with(getApplicationContext()).load(profile).into(viewHolder.messengerImageView);
                    }
                } else {
                    Log.d("sender", "is null");
                    viewHolder.messengerTextView.setText(messages.get(position).getName());
                    // Glide.with(getApplicationContext()).load(messages.get(position).getPhotoUrl()).into(viewHolder.messengerImageView);
                }
                viewHolder.messengerTextView.setVisibility(TextView.VISIBLE);
            }
        }

        @Override
        public int getItemCount() { return messages.size(); }

        @Override
        public int getItemViewType(int position) {
            ChatMessage message = messages.get(position);
            if (message.getName().equals(mSender)) return MY_MESSAGE;
            else return NOT_MY_MESSAGE;
        }
    }

    private static final String TAG = "MainActivity";

    private String mUsername;
    private String mObjectId;   // TODO: SharedPreference
    private String mPhotoUrl;
    private SharedPreferenceManager mSharedPreferenceManager;
    private GoogleApiClient mGoogleApiClient;

    private ArrayList<ChatMessage> mChatMessages = new ArrayList<>();

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;

    private DatabaseReference mFirebaseDatabaseReference;
    // private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;
    private RecyclerView.Adapter<MessageViewHolder> mRecyclerViewAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;

    private Socket mSocket;
    private String mSender;
    private String mRoomId;
    private int mChatroomCount;

    private ChatDbHelper mDbHelper;
    private FriendDbHelper mFriendDbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        mUsername = firebaseUser.getDisplayName();

        if (firebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = firebaseUser.getPhotoUrl().toString();
        }

        mSender = firebaseUser.getUid();
        mRoomId = getIntent().getStringExtra("_id");
        Log.d("ChatActivity", "roomId: " + mRoomId);

        // TODO set unreadCount to 0
        mSharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        mSharedPreferenceManager.setUnreadChatCount(mRoomId, 0);
        ChatFragment.adapter.notifyDataSetChanged();

        for (mChatroomCount = 0; mChatroomCount < ChatFragment.mChatRooms.size(); mChatroomCount++) {
            if (ChatFragment.mChatRooms.get(mChatroomCount)._id.equals(mRoomId)) break;
        }
        if (mChatroomCount == ChatFragment.mChatRooms.size()) {
            ChatFragment.mChatRooms.add(new ChatroomMetaBody(mRoomId, 2, "", System.currentTimeMillis()));
            ChatFragment.adapter.notifyDataSetChanged();
        }

        SharedPreferenceManager.getInstance(getApplicationContext()).setRoomStatus(mRoomId, true);

        // SQLite
        mDbHelper = new ChatDbHelper(getApplicationContext());
        long lastChatTimestamp = mDbHelper.getLastChatTimestamp(mRoomId);
        Log.d("LastChatTimestamp", Long.toString(lastChatTimestamp));

        mFriendDbHelper = new FriendDbHelper(getApplicationContext());

        // socket.io
        try {
            mSocket = IO.socket("http://52.78.4.96:3000");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    // TODO
                    Log.d("Socket.io", "Socket connected.");
                    // JSONObject data = (JSONObject) args[0];

                    JSONObject message = new JSONObject();
                    try {
                        Log.d("Socket.io", "inside try");
                        message.put("room", mRoomId);
                        message.put("sender", mSender);
                        // message.put("message", "Hello Specker!");
                        mSocket.emit("identification", message);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            mSocket.on("chat_message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String author = (String) data.get("author");
                        if (author.equals(mSender)) return;
                        String message = (String) data.get("message");
                        long timestamp = (long) data.get("timestamp");
                        String profileUrl = (String) data.get("profile");
                        mChatMessages.add(new ChatMessage(message, author, profileUrl, ""));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                        if (mDbHelper.ensureTimestamp(mRoomId, timestamp)) {
                            long newRowId = mDbHelper.insertChat(mRoomId, author, message, timestamp);
                            Log.d("INSERT_CHAT_SOCKET", "New Row Id: " + newRowId + ", message: " + message + ", timestamp: " + Long.toString(timestamp));
                        }
                    }
                    catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }
        catch (URISyntaxException ex) {
            ex.printStackTrace();
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        // mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        /* FIXME
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, GlobalVariable.DEFAULT_MSG_LENGTH_LIMIT))});
        */
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send messages on click.
                ChatMessage friendlyMessage = new ChatMessage(
                        mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl,
                        null /* no image */);
                /*
                mFirebaseDatabaseReference.child(GlobalVariable.MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                */
                String message = mMessageEditText.getText().toString();

                JSONObject json = new JSONObject();
                try {
                    long timestamp = System.currentTimeMillis();
                    json.put("room", mRoomId);
                    json.put("message", message);
                    json.put("sender", mSender);
                    json.put("timestamp", timestamp);
                    mSocket.emit("chat_message", json);
                    mChatMessages.add(new ChatMessage(message, mSender, "", ""));
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    long newRowId = mDbHelper.insertChat(mRoomId, mSender, message, timestamp);
                    Log.d("INSERT_CHAT_BUTTON", "New Row Id: " + newRowId + ", message: " + message + ", timestamp: " + Long.toString(timestamp));
                    // ChatFragment.mChatRooms.get(mChatroomCount)._id = mSender;
                    // ChatFragment.mChatRooms.get(mChatroomCount).lastChat = message;
                    // ChatFragment.mChatRooms.get(mChatroomCount).lastTimestamp = timestamp;
                    // ChatFragment.adapter.notifyDataSetChanged();
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }

                mMessageEditText.setText("");
            }
        });

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Select image for image message on click.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GlobalVariable.REQUEST_IMAGE);
            }
        });


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // mChatMessages = new ArrayList<>();
        mRecyclerViewAdapter = new RecyclerAdapter(mChatMessages);

        // TODO
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        // Cursor cursor = mDbHelper.getChatHistory(mRoomId);
        ArrayList<ChatMessage> mChatHistory = mDbHelper.getChatHistory(mRoomId);
        mChatMessages.addAll(mChatHistory);
        /*
        for (int i = 0; i < cursor.getCount(); i++) {
            String author = cursor.getString(cursor.getColumnIndex("author"));
            String message = cursor.getString(cursor.getColumnIndex("message"));
            long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
            mChatMessages.add(new ChatMessage(message, author, "", ""));
            cursor.moveToNext();
        }
        */
        mRecyclerViewAdapter.notifyDataSetChanged();

        firebaseUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (!task.isSuccessful()) return;
                        String token = task.getResult().getToken();
                        ChatRequest chatRequest = ChatRequest.retrofit.create(ChatRequest.class);
                        Call<GetChatResponse> call = chatRequest.getChat(token, new GetChatBody(mSender, mRoomId, lastChatTimestamp));
                        // Observable<GetChatResponse> observable = chatRequest.getChat(token, new GetChatBody(firebaseUser.getUid(), mRoomId, 0));
                        new GetChatTask(ChatActivity.this, mChatMessages).execute(call);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                    }
                });

        // mRecyclerViewAdapter.notifyDataSetChanged();

        // mRecyclerViewAdapter = new RecyclerView.Adapter<MessageViewHolder>();
        /*
        try {
            mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                    ChatMessage.class,
                    R.layout.item_message,
                    MessageViewHolder.class,
                    mFirebaseDatabaseReference.child(GlobalVariable.MESSAGES_CHILD)
            ) {

                @Override
                protected ChatMessage parseSnapshot(DataSnapshot snapshot) {
                    ChatMessage friendlyMessage = super.parseSnapshot(snapshot);
                    if (friendlyMessage != null) {
                        friendlyMessage.setId(snapshot.getKey());
                    }
                    return friendlyMessage;
                }

                @Override
                protected void populateViewHolder(final MessageViewHolder viewHolder, ChatMessage friendlyMessage, int position) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (friendlyMessage.getText() != null) {
                        viewHolder.messageTextView.setText(friendlyMessage.getText());
                        viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                        viewHolder.messageImageView.setVisibility(ImageView.GONE);
                    } else {
                        String imageUrl = friendlyMessage.getImageUrl();
                        if (imageUrl.startsWith("gs://")) {
                            StorageReference storageReference = FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(imageUrl);
                            storageReference.getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String downloadUrl = task.getResult().toString();
                                                Glide.with(viewHolder.messageImageView.getContext())
                                                        .load(downloadUrl)
                                                        .into(viewHolder.messageImageView);
                                            } else {
                                                Log.w(TAG, "Getting download url was not successful.",
                                                        task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Glide.with(viewHolder.messageImageView.getContext())
                                    .load(friendlyMessage.getImageUrl())
                                    .into(viewHolder.messageImageView);
                        }
                        viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                        viewHolder.messageTextView.setVisibility(TextView.GONE);
                    }


                    viewHolder.messengerTextView.setText(friendlyMessage.getName());
                    if (friendlyMessage.getPhotoUrl() == null) {
                        viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                                R.drawable.ic_account_circle_black_36dp));
                    } else {
                        Glide.with(getApplicationContext())
                                .load(friendlyMessage.getPhotoUrl())
                                .into(viewHolder.messengerImageView);
                    }

                    if (friendlyMessage.getText() != null) {
                        // write this message to the on-device index
                        FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));
                    }

                    // log a view action on it
                    FirebaseUserActions.getInstance().end(getMessageViewAction(friendlyMessage));

                }
            };
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        */

        mRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            /*
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mRecyclerViewAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (messageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
            */
            @Override
            public void onChanged() {
                super.onChanged();
                mMessageRecyclerView.scrollToPosition(mRecyclerViewAdapter.getItemCount() - 1);
            }
        });

        /*
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        */

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mRecyclerViewAdapter);  // mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
        if (firebaseUser == null) finish();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /* TODO: startActivityForResult
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        String mLastChat = "";
        intent.putExtra("lastChat", mLastChat);
        super.onBackPressed();
    }
    */

    @Override
    public void onDestroy() {
        JSONObject disconnect = new JSONObject();
        try {
            disconnect.put("room", mRoomId);
            disconnect.put("sender", mSender);
            mSocket.emit("disconnect", disconnect);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        SharedPreferenceManager.getInstance(getApplicationContext()).setRoomStatus(mRoomId, false);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVariable.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();

                    ChatMessage tempMessage = new ChatMessage(null, mUsername, mPhotoUrl,
                            GlobalVariable.LOADING_IMAGE_URL);
                    mFirebaseDatabaseReference.child(GlobalVariable.MESSAGES_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mSender)
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Image upload task was successful.");
                            /*
                             * TODO : This method should only be accessed from tests or within private scope.
                             * task.getResult().getMetadata()
                            ChatMessage friendlyMessage =
                                    new ChatMessage(null, mUsername, mPhotoUrl,
                                            task.getResult().getMetadata().getDownloadUrl()
                                                    .toString());
                            mFirebaseDatabaseReference.child(GlobalVariable.MESSAGES_CHILD).child(key)
                                    .setValue(friendlyMessage);
                            */
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }


    private Action getMessageViewAction(ChatMessage friendlyMessage) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(friendlyMessage.getName(), GlobalVariable.MESSAGE_URL.concat(friendlyMessage.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(ChatMessage friendlyMessage) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername.equals(friendlyMessage.getName()))
                .setName(friendlyMessage.getName())
                .setUrl(GlobalVariable.MESSAGE_URL.concat(friendlyMessage.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(GlobalVariable.MESSAGE_URL.concat(friendlyMessage.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(friendlyMessage.getText())
                .setUrl(GlobalVariable.MESSAGE_URL.concat(friendlyMessage.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    public class GetChatTask extends AsyncTask<Call, Void, ArrayList<ChatLog>> {

        private Context context;
        private ArrayList<ChatMessage> array;

        public GetChatTask(Context context, ArrayList<ChatMessage> chatlogs) {
            this.context = context;
            array = chatlogs;
        }

        @Override
        protected ArrayList<ChatLog> doInBackground(Call... params) {
            Call<GetChatResponse> call = params[0];
            ArrayList<ChatLog> result = new ArrayList<>();
            try {
                Response<GetChatResponse> response = call.execute();
                if (response.isSuccessful()) {
                    result = response.body().logs;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<ChatLog> result) {
            super.onPostExecute(result);
            for (ChatLog log: result) {
                long _timestamp = Long.parseLong(log.timestamp);
                if (mDbHelper.ensureTimestamp(mRoomId, _timestamp)) {
                    long newRowId = mDbHelper.insertChat(mRoomId, log.author, log.content, _timestamp);
                    Log.d("INSERT_CHAT_HTTP", "New Row Id: " + newRowId + ", message: " + log.content + ", timestamp: " + log.timestamp);
                    array.add(new ChatMessage(log.content, log.author, "", ""));
                }
            }
            mRecyclerViewAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }
}
