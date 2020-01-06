package com.example.oauth2;

import android.util.Base64;

import androidx.annotation.Nullable;

import com.example.oauth2.Model.SharedPreferences.TokenManager;
import com.example.oauth2.Model.Token;
import com.example.oauth2.api.WebServiceApi;
import com.example.oauth2.api.WebServiceOuath2;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;

public class CustomAuthenticator implements Authenticator {

    private TokenManager tokenManager;
    private static CustomAuthenticator Instance;

    private CustomAuthenticator(TokenManager tokenManager){
        this.tokenManager = tokenManager;
    }

   public  static  synchronized CustomAuthenticator getInstance(TokenManager tokenManager){
        if(Instance == null){
            Instance= new CustomAuthenticator(tokenManager);
        }
        return Instance;
   }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, Response response) throws IOException {
        String authHeader = "Basic " + Base64.encodeToString("androidApp:123".getBytes(), Base64.NO_WRAP);
        Token token = tokenManager.getToken();
        Call<Token> call = WebServiceOuath2.getInstance().createService(WebServiceApi.class).obtenerTokenconRefreshToken(authHeader,token.getRefreshToken(),"refresh_token");
        retrofit2.Response<Token> response1  = call.execute();
        if(response1.isSuccessful()) {
            Token newToken = response1.body();
            tokenManager.saveToken(newToken);
            return response.request().newBuilder().header("Authorization","Bearer "+response1.body().getAccessToken()).build();
        }else{
            return null;
        }
    }
}
