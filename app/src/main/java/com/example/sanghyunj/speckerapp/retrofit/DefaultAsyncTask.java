package com.example.sanghyunj.speckerapp.retrofit;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by rapsealk on 2017. 10. 9..
 */

public class DefaultAsyncTask<T> extends AsyncTask<Call, Void, String> {

    private Context context;

    public DefaultAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Call... params) {
        Call<T> call = params[0];
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                if (response.body() != null) return response.code() + " " + response.body().toString();
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
        super.onPostExecute(result);
        Log.d("RESPONSE", result.toString());
        Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
    }
}
