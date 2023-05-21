package com.glgamedev.sportscompetitions.Network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;

public class NetworkClient {
    public static OkHttpClient client = new OkHttpClient();
    public static MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static interface OnResponseListener {
        public void onSuccessful(int code, String data);
        public void onError(int code, String data);
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

    public static String resp1 = "{\n" +
            "  \"isSuccess\": true,\n" +
            "  \"message\": \"успешно\",\n" +
            "  \"data\": {" +
                "  \"competitions\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"Бег\",\n" +
                "      \"type\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"name\": \"Стрельба\",\n" +
                "      \"type\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"user\": {\n" +
                "    \"id\": 1,\n" +
                "    \"surname\": \"Админ\",\n" +
                "    \"name\": \"Админ\",\n" +
                "    \"age\": 13,\n" +
                "    \"login\": 555666,\n" +
                "    \"teamId\": 1,\n" +
                "    \"role\": 0,\n" +
                "    \"eventId\": 1\n" +
                "  },\n" +
                "  \"users\": [{\n" +
                "    \"id\": 1,\n" +
                "    \"login\": 567123,\n" +
                "    \"surname\": \"Лысаков\",\n" +
                "    \"name\": \"Илья\",\n" +
                "    \"age\": 13,\n" +
                "    \"teamId\": 1\n" +
                "  }, {\n" +
                "    \"id\": 2,\n" +
                "    \"login\": 445675,\n" +
                "    \"surname\": \"Игнатов\",\n" +
                "    \"name\": \"Александр\",\n" +
                "    \"age\": 14,\n" +
                "    \"teamId\": 2\n" +
                "  }],\n" +
                "  \"teamResults\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"Команда 1\",\n" +
                "      \"league\": 1,\n" +
                "      \"results\": [\n" +
                "        {\n" +
                "          \"competitionId\": 1,\n" +
                "          \"total\": \"10:02\",\n" +
                "          \"place\": 2,\n" +
                "          \"memberResults\": [\n" +
                "            {\n" +
                "              \"memberId\": 1,\n" +
                "              \"surname\": \"Лысаков\",\n" +
                "              \"name\": \"Илья\",\n" +
                "              \"value\": {\n" +
                "                \"steps\": [\n" +
                "                  \"10\"\n" +
                "                ],\n" +
                "                \"total\": \"10\"\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"competitionId\": 2,\n" +
                "          \"total\": \"20\",\n" +
                "          \"place\": 1,\n" +
                "          \"memberResults\": [\n" +
                "            {\n" +
                "              \"memberId\": 1,\n" +
                "              \"surname\": \"Лысаков\",\n" +
                "              \"name\": \"Илья\",\n" +
                "              \"value\": {\n" +
                "                \"steps\": [\n" +
                "                  \"10\",\n" +
                "                  \"10\",\n" +
                "                  \"0\"\n" +
                "                ],\n" +
                "                \"total\": \"20\"\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"name\": \"Команда 2\",\n" +
                "      \"league\": 1,\n" +
                "      \"results\": [\n" +
                "        {\n" +
                "          \"competitionId\": 1,\n" +
                "          \"total\": \"8:43\",\n" +
                "          \"place\": 1,\n" +
                "          \"memberResults\": [\n" +
                "            {\n" +
                "              \"memberId\": 2,\n" +
                "              \"surname\": \"Игнатов\",\n" +
                "              \"name\": \"Александр\",\n" +
                "              \"value\": {\n" +
                "                \"steps\": [\n" +
                "                  \"8\"\n" +
                "                ],\n" +
                "                \"total\": \"8\"\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"competitionId\": 2,\n" +
                "          \"total\": \"15\",\n" +
                "          \"place\": 2,\n" +
                "          \"memberResults\": [\n" +
                "            {\n" +
                "              \"memberId\": 2,\n" +
                "              \"surname\": \"Игнатов\",\n" +
                "              \"name\": \"Александр\",\n" +
                "              \"value\": {\n" +
                "                \"steps\": [\n" +
                "                  \"10\",\n" +
                "                  \"3\",\n" +
                "                  \"2\"\n" +
                "                ],\n" +
                "                \"total\": \"15\"\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
            "}\n";
}
