package com.marinabragatti.beautysaloon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        autenticacao = ConfigFirebase.getFirebaseAuth();
        searchView = findViewById(R.id.materialSearch);

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Beauty Saloon");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);

        //Configuração Menu Pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisar);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuSair :
                deslogarUser();
                break;
            case R.id.menuConfig :
                abrirConfig();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUser(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfig(){
        startActivity(new Intent(HomeActivity.this, ConfigUserActivity.class));
    }

}
