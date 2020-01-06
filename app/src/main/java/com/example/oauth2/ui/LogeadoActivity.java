package com.example.oauth2.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.oauth2.Model.MovimientoBancario;
import com.example.oauth2.Model.SharedPreferences.TokenManager;
import com.example.oauth2.Model.Token;
import com.example.oauth2.Model.User;
import com.example.oauth2.R;
import com.example.oauth2.api.WebServiceApi;
import com.example.oauth2.api.WebServiceOuath2;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.oauth2.Model.SharedPreferences.TokenManager.SHARED_PREFERENCES;

public class LogeadoActivity extends AppCompatActivity {

    private Button btnVerTodosLosMovimientosBancarios;
    private Button btnVerTodosLosMovimientosBancariosUser;
    private TokenManager tokenManager;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logeado);
        setUpView();
        activity=this;
    }

    private void setUpView() {
     tokenManager = TokenManager.getInstance(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE));
     btnVerTodosLosMovimientosBancarios = findViewById(R.id.btnVerTodosLosMovimientosBancarios);
     btnVerTodosLosMovimientosBancariosUser = findViewById(R.id.btnVerTodosLosMovimientosBancariosUser);

     btnVerTodosLosMovimientosBancariosUser.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             verTodosMovimientosUser();
         }
     });

     btnVerTodosLosMovimientosBancarios.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             verTodosMovimientos();
         }
     });
    }

    private void verTodosMovimientos() {
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

    private void verTodosMovimientosUser() {
        User user = new User();
        user.setId(55l);
        Call<List<MovimientoBancario>> call = WebServiceOuath2.getInstance().createServiceWhitAuouth2(WebServiceApi.class,tokenManager).obtenerMovimientosUser(user);
        call.enqueue(new Callback<List<MovimientoBancario>>() {
            @Override
            public void onResponse(Call<List<MovimientoBancario>> call, Response<List<MovimientoBancario>> response) {
                if(response.code()==200){
                    for(int i =0 ;i<response.body().size();i++){
                        Log.d("TAG1","UserID: "+response.body().get(i).getUserID()+" Importe: "+response.body().get(i).getImporte()+" Nombre: "+response.body().get(i).getName());
                    }
                }else if(response.code()==404){
                    Log.d("TAG1","No hay movimientos");

                }else if(response.code()==401){

                     Token newToken = new Token();
                     newToken.setAccessToken("");
                     newToken.setRefreshToken("");
                     tokenManager.saveToken(newToken);
                     try{
                         JSONObject jsonObject = new JSONObject(response.errorBody().toString());
                         Log.d("TAG1","Invalid Access Token: " + jsonObject.getString("error"));
                     }catch (Exception e){
                         Log.d("TAG1","invalid Access Token"+e.getMessage());
                     }
                     activity.finish();
                }else{
                    Log.d("TAG1","Error");
                }
            }

            @Override
            public void onFailure(Call<List<MovimientoBancario>> call, Throwable t) {

            }
        });
    }
}
