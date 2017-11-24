package com.example.sanghyunj.speckerapp.service;

/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.sanghyunj.speckerapp.MainActivity;
import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.activity.ChatActivity;
import com.example.sanghyunj.speckerapp.util.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String room = data.get("room");
        String sender = data.get("sender");
        String message = data.get("message");
        long timestamp = Long.parseLong(data.get("timestamp"));

        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(sender) &&
                !SharedPreferenceManager.getInstance(getApplicationContext()).getRoomStatus(room)) {
            Intent intent = new Intent(this, ChatActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra("_id", room);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0x1002, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(sender + "가 메시지를 보냈습니다. \"" + message + "\"");
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(0x1001, notificationBuilder.build());
        }

        Intent bIntent = new Intent();
        bIntent.setAction("com.example.sanghyunj.speckerapp.RECEIVE_CHAT");
        bIntent.putExtra("room", room);
        bIntent.putExtra("message", message);
        bIntent.putExtra("timestamp", timestamp);
        Log.d("MessagingService", "sendBroadcast with action RECEIVE_CHAT");
        sendBroadcast(bIntent);

        /* TODO
         * 1) WAKE_LOCK
         * 2) TOAST
         */

        /*
        String body = remoteMessage.getNotification().getBody();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher) // 알림 영역에 노출 될 아이콘.
                .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
                .setContentText(body.toString()); // Firebase Console 에서 사용자가 전달한 메시지내용

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(0x1001, notificationBuilder.build());
        */
    }
}
