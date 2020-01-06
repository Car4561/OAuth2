package com.example.oauth2.Model.SharedPreferences;

import android.content.SharedPreferences;

import com.example.oauth2.Model.Token;

public class TokenManager {

    public static String SHARED_PREFERENCES  = "SHARED_PREFERENCES";
    private static final String SHARED_PREFERENCES_ACCESS_TOKEN = "SHARED_PREFERENCES_ACCESS_TOKEN";
    private static final String SHARED_PREFERENCES_REFRESH_TOKEN = "SHARED_PREFERENCES_REFRESH_TOKEN";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static TokenManager instance = null;

    private TokenManager(SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();

    }

    public static synchronized TokenManager getInstance(SharedPreferences sharedPreferences){
        if(instance==null){
            instance = new TokenManager(sharedPreferences);
        }
        return  instance;
    }

    public void saveToken(Token token){
        editor.putString(SHARED_PREFERENCES_ACCESS_TOKEN,token.getAccessToken());
        editor.putString(SHARED_PREFERENCES_REFRESH_TOKEN,token.getRefreshToken());
        editor.commit();

    }
    public Token getToken(){
        Token token = new Token();
        token.setAccessToken(sharedPreferences.getString(SHARED_PREFERENCES_ACCESS_TOKEN,null));
        token.setRefreshToken(sharedPreferences.getString(SHARED_PREFERENCES_REFRESH_TOKEN,null));
        return token;
    }
}
