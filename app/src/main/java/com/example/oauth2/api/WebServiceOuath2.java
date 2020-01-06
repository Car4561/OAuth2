package com.example.oauth2.api;

import com.example.oauth2.CustomAuthenticator;
import com.example.oauth2.Model.SharedPreferences.TokenManager;
import com.example.oauth2.Model.Token;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceOuath2 {
    private static final String BASE_URL="http://10.0.2.2:8045/";
    private static HttpLoggingInterceptor httpLoggingInterceptor;
    private OkHttpClient.Builder okHtppbuilder;
    private Retrofit retrofit;
    private static  WebServiceOuath2 instance;

    private WebServiceOuath2 (){
        httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        okHtppbuilder = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor);
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(okHtppbuilder.build()).addConverterFactory(GsonConverterFactory.create()).build();


    }
    public static synchronized WebServiceOuath2 getInstance(){
        if(instance==null){
            instance = new WebServiceOuath2();
        }
        return instance;
    }
    public <S> S createService(Class<S> serviceClass){
        return  retrofit.create(serviceClass);
    }

    public <S> S createServiceWhitAuouth2(Class<S> serviceClass, final TokenManager tokenManager){
        final  OkHttpClient  newClient = okHtppbuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request requestOriginal = chain.request();
                Request.Builder builder = requestOriginal.newBuilder();

                if(tokenManager.getToken().getAccessToken() !=null){
                    builder.addHeader("Authorization","Bearer "+tokenManager.getToken().getAccessToken());
                }
                Request request  = builder.build();
                return chain.proceed(request);
            }
        }).authenticator(CustomAuthenticator.getInstance(tokenManager)).build();
        return retrofit.newBuilder().client(newClient).build().create(serviceClass);

    }


}
