package com.example.oauth2.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.oauth2.Model.MovimientoBancario;
import com.example.oauth2.Model.SharedPreferences.TokenManager;
import com.example.oauth2.Model.Token;
import com.example.oauth2.Model.User;
import com.example.oauth2.R;
import com.example.oauth2.api.WebServiceApi;
import com.example.oauth2.api.WebServiceOuath2;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.oauth2.Model.SharedPreferences.TokenManager.SHARED_PREFERENCES;

public class MainActivity extends AppCompatActivity {
    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnObtenerToken;
    private Button btnCrearUsuario;
    private Button btnVerTodosUsuarios;
    private Button btnVerTodosLosMovimientosBancarios;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewUp();



    }
    private void setViewUp() {
        tokenManager = TokenManager.getInstance(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE));
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnObtenerToken = findViewById(R.id.btnObtenerToken);
        btnCrearUsuario = findViewById(R.id.btnCrearUsuario);
        btnVerTodosUsuarios = findViewById(R.id.btnVerTodosUsuarios);
        btnVerTodosLosMovimientosBancarios = findViewById(R.id.btnVerTodosLosMovimientosBancarios);

        btnVerTodosUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerTodosUsuarios();
            }
        });

        btnCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearUsuario();
            }
        });


        btnObtenerToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerToken();

            }
        });

        btnVerTodosLosMovimientosBancarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 verMovimientosBancarios();
            }
        });




    }

    private void verMovimientosBancarios() {
        String accesToken = "Bearer "+tokenManager.getToken().getAccessToken();
        Call<List<MovimientoBancario>> call = WebServiceOuath2.getInstance().createService(WebServiceApi.class).obtenerMovimientos(accesToken);
        call.enqueue(new Callback<List<MovimientoBancario>>() {
            @Override
            public void onResponse(Call<List<MovimientoBancario>> call, Response<List<MovimientoBancario>> response) {
                if(response.code()==200){
                    for(int i = 0 ; i<response.body().size();i++){
                        Log.d("TAG1","User ID: "+response.body().get(i).getUserID()+" Importe: "+response.body().get(i).getImporte()+" Nombre: "+response.body().get(i).getName());
                    }
                }else if (response.code()==404){
                    Log.d("TAG1","Error");
                }
            }

            @Override
            public void onFailure(Call<List<MovimientoBancario>> call, Throwable t) {

            }
        });


    }

    private void obtenerToken() {
        String authHeader = "Basic "+ Base64.encodeToString("androidApp:123".getBytes(),Base64.NO_WRAP);
        Call<Token> call = WebServiceOuath2.getInstance().createService(WebServiceApi.class).obtenerToken(authHeader,
                               txtUsername.getText().toString(),txtPassword.getText().toString(),"password");
        call.enqueue(new Callback<Token>() {
            Token token =new Token();
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.code()==200){
                  Log.d("TAG1","Access Token: "+response.body().getAccessToken()+"Refresh Token: "+ response.body().getRefreshToken());
                  token= response.body();
                  tokenManager.saveToken(token);
                  startActivity(new Intent(getApplicationContext(),LogeadoActivity.class));

                }else{
                    Log.d("TAG1","Error");

                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });







    }

    private void crearUsuario() {
    User user = new User();
    user.setUsername(txtUsername.getText().toString());
    user.setPassword(txtUsername.getText().toString());
    Call<Void> call = WebServiceOuath2.getInstance().createService(WebServiceApi.class).crearUsuario(user);
    call.enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if(response.code()==201){
                Log.d("TAG1","Usuario Creado Correctamente");

            }else{
                Log.d("TAG1","Error");
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {

        }
    });


    }

    private void VerTodosUsuarios() {
        Call<List<User>> call = WebServiceOuath2.getInstance().createService(WebServiceApi.class).obtenerUsuarios();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.code()==200) {
                   for(int i = 0;i<response.body().size();i++) {
                      Log.d("TAG1","Username: "+response.body().get(i).getUsername());

                   }
                }
                else {
                    Log.d("TAG1","Error");

                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });




    }

}
