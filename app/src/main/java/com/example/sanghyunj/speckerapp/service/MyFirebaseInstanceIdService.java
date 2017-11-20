/**
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
package com.example.sanghyunj.speckerapp.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.sanghyunj.speckerapp.retrofit.AuthWithToken;
import com.example.sanghyunj.speckerapp.retrofit.Body.UpdateToken;
import com.example.sanghyunj.speckerapp.retrofit.DefaultAsyncTask;
import com.example.sanghyunj.speckerapp.retrofit.DefaultResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";

    /**
     * The Application's current Instance ID token is no longer valid and thus a new one must be requested.
     */
    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or after a refresh this is
        // where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance().subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);

        // FIXME
        Log.d("Firebase InstanceId", "instanceId: " + token);

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            mFirebaseUser.getToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("InstanceId Result", "Succeed");
                                String header = task.getResult().getToken();
                                String uid = mFirebaseUser.getUid();
                                AuthWithToken authWithToken = AuthWithToken.retrofit.create(AuthWithToken.class);
                                Call<DefaultResponse> call = authWithToken.updateToken("", new UpdateToken(uid, token));
                                new DefaultAsyncTask<DefaultResponse>(getApplicationContext()).execute(call);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("InstanceId Result", "Failed");
                        }
                    });
        }
    }
}
