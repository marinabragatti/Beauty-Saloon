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
import com.google.firebase.auth.FirebaseUser;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        autenticacao = ConfigFirebase.getFirebaseAuth();

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Beauty Saloon - Empresa");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);
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
            case R.id.menuNovoProduto :
                addNovoProduto();
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
        startActivity(new Intent(EmpresaActivity.this, ConfigEmpresaActivity.class));
    }

    private void addNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }
}
