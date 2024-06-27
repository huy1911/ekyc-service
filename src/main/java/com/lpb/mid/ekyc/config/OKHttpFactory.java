package com.lpb.mid.ekyc.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class OKHttpFactory {

    private static OKHttpFactory ourInstance = new OKHttpFactory();

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .connectionPool(
                    new ConnectionPool(150,
                            5L, TimeUnit.MINUTES))
            .build();

    public static OKHttpFactory getInstance() {
        return ourInstance;
    }

    private OKHttpFactory() {
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }
}
