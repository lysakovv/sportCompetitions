package com.glgamedev.sportscompetitions.Network;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkClient {
    public static OkHttpClient client = new OkHttpClient();
    public static MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface OnResponseListener {
        void onSuccessful(int code, String data);

        void onError(int code, String data);
    }

    public static String url = "http://10.0.2.2:7233/";

    public static void Post(String method, String data, OnResponseListener listener) {
        RequestBody body = RequestBody.create(data, JSON);

        Request request = new Request.Builder()
                .url(url + method)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        listener.onError(response.code(), response.message());
                    }

                    listener.onSuccessful(response.code(), responseBody.string());
                }
            }
        });
    }

    public static void Get(String method, OnResponseListener listener) {
        Request request = new Request.Builder()
                .url(url + method)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        listener.onError(response.code(), response.message());
                    }

                    listener.onSuccessful(response.code(), responseBody.string());
                }
            }
        });
    }
}
