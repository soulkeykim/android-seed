package com.{{company_name}}.{{app_package_name_prefix}}.network;

import com.squareup.okhttp.OkHttpClient;

import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.GsonConverterFactory;
import rx.Observable;
import java.util.concurrent.TimeUnit;

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
        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        client.setWriteTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        client.setConnectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        return client;
    }

}
