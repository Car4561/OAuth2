package com.example.oauth2.api;

import com.example.oauth2.Model.MovimientoBancario;
import com.example.oauth2.Model.Token;
import com.example.oauth2.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface WebServiceApi {

    @GET("/api/users")
    Call<List<User>> obtenerUsuarios();


    @POST("/api/create_user")
    Call<Void> crearUsuario(@Body User user);


    @FormUrlEncoded
    @POST("oauth/token")
    Call<Token> obtenerToken(@Header("Authorization") String autorization,
                             @Field("username") String username,
                             @Field("password") String password,
                             @Field("grant_type") String grantType);

    @GET("/api/oauth2/movimiento_bancario")
    Call<List<MovimientoBancario>> obtenerMovimientos(@Header("Authorization") String accesToken);


    @FormUrlEncoded
    @POST("oauth/token")
    Call<Token> obtenerTokenconRefreshToken(@Header("Authorization") String autorization,
                             @Field("refresh_token") String refreshToken,
                             @Field("grant_type") String grantType);


    @POST("/api/oauth2/movimiento_bancario_user")
    Call<List<MovimientoBancario>> obtenerMovimientosUser(@Body User user);
}
