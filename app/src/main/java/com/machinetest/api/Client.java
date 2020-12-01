package com.machinetest.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    public static Retrofit retrofit = null;
    public static final String BaseUrl="https://api.imgur.com/3/gallery/search/";
    public static final String auth="Client-ID 137cda6b5008a7c";
    public static Retrofit getRetrofit() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        Gson gson = gsonBuilder.create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(getHeader(auth))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }
    public static OkHttpClient getHeader(final String authorizationValue) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = null;
                        if (authorizationValue != null) {
                            request = chain.request();
                            Request.Builder builder = request.newBuilder()
                                    .addHeader("Authorization", authorizationValue);
                            request = builder.build();
                        }
                        return chain.proceed(request);
                    }
                })
                .build();
        return okClient;

    }

}






