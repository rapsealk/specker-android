package com.example.sanghyunj.speckerapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by rapsealk on 2017. 11. 8..
 */
/*
public class MessengerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Socket mSocket = IO.socket("http://52.78.4.96:3000");
                    mSocket.connect();
                    mSocket.on(uid, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                String room = (String) data.get("room");
                                String author = (String) data.get("author");
                                String message = (String) data.get("message");
                            } catch (JSONException exception) {
                                exception.printStackTrace();
                            }
                        }
                    });
                } catch (URISyntaxException exception) {
                    exception.printStackTrace();
                }
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
*/