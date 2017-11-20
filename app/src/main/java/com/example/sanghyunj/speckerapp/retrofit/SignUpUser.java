package com.example.sanghyunj.speckerapp.retrofit;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rapsealk on 2017. 10. 14..
 */

public class SignUpUser implements Serializable {

    public String email;
    public String password;
    public String age;
    public String sex;
    public String name;
    public String phone;
    public Address address;
    public ArrayList<String> tag;

    public static SignUpUser getBuilder() { return new SignUpUser(); }

    private SignUpUser() {
        this.email = "";
        this.password = "";
        this.age = "";
        this.sex = "";
        this.name = "";
        this.phone = "";
        this.address = null;
        this.tag = new ArrayList<String>();
    }

    public SignUpUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public SignUpUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public SignUpUser setAge(String age) {
        this.age = age;
        return this;
    }

    public SignUpUser setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public SignUpUser setName(String name) {
        this.name = name;
        return this;
    }

    public SignUpUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    /*
    public SignUpUser setAddress(Address address) {
        this.address = address;
        return this;
    }
    */

    public SignUpUser setAddress(String placeId, String label, String lat, String lng) {
        this.address = new Address(placeId, label, lat, lng);
        return this;
    }
}

class Address {
    public String placeId;
    public String label;
    public String lat, lng;
    public Address(String placeId, String label, String lat, String lng) {
        this.placeId = placeId;
        this.label = label;
        this.lat = lat;
        this.lng = lng;
    }
}

/* TODO
firebaseUser.getToken(true)
    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
        @Override
        public void onComplete(@NonNull Task<GetTokenResult> task) {
            if (task.isSuccessful()) {
                String idToken = task.getResult().getToken();
                AuthWithToken authWithToken = AuthWithToken.retrofit.create(AuthWithToken.class);
                final Call<DefaultResponse> call = authWithToken.signUp(idToken, SignUpUser.getBuilder()
                        .setEmail(firebaseUser.getEmail())
                        .setName(firebaseUser.getDisplayName())
                        .setAddress("", "", "", ""));
                new NetworkCall().execute(call);
            } else {
                Toast.makeText(getContext(), "Failed to retrieve user token.", Toast.LENGTH_SHORT).show();
            }
        }
    });
 */