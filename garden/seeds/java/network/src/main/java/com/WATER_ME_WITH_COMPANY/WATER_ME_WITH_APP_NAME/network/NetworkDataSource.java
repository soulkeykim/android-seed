package com.{{company_name}}.{{app_package_name_prefix}}.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkDataSource implements DataSource {

    private static final long CONNECTION_TIMEOUT = 10;

    final ApiService mService;

    public NetworkDataSource(String endpoint) {
        OkHttpClient client = createDefaultHttpClient();

        mService = new Retrofit.Builder()
                .baseUrl(endpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(ApiService.class);
    }

    private OkHttpClient createDefaultHttpClient() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .build();
        return client;
    }

}
