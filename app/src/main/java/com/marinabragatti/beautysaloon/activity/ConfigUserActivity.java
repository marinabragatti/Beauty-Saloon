package com.marinabragatti.beautysaloon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.marinabragatti.beautysaloon.helper.UsuarioFirebase;
import com.marinabragatti.beautysaloon.model.Empresa;
import com.marinabragatti.beautysaloon.model.Usuario;
import com.squareup.picasso.Picasso;

public class ConfigUserActivity extends AppCompatActivity {

    private EditText campoNome, campoEndereco;
    private DatabaseReference firebaseRef;
    private String idUserLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_user);

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();
        idUserLogado = UsuarioFirebase.getIdUser();//Recupera user id
        firebaseRef = ConfigFirebase.getFirebase();//Recupera referência do firebase

        //Recuperar os dados na Configuração da Empresa
        recuperarDadosUser();
    }

    public void validarDadosUser(View view){
        //Valida se os campos foram preenchidos
        String nome = campoNome.getText().toString();
        String endereco = campoEndereco.getText().toString();

        if(!nome.isEmpty()){
            if(!endereco.isEmpty()){
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUserLogado);
                usuario.setNome(nome);
                usuario.setEndereco(endereco);
                usuario.salvar();
                finish();
            }else{
                exibirToast("Digite seu endereço");
            }
        }else{
            exibirToast("Digite seu nome");
        }
    }

    public void recuperarDadosUser(){
        DatabaseReference empresaRef = firebaseRef.child("usuarios").child(idUserLogado);//Recupera referência do user logado
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);//Recupero o objeto user salvo no firebase
                    campoNome.setText(usuario.getNome());
                    campoEndereco.setText(usuario.getEndereco());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void exibirToast(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    public void inicializarComponentes(){
        campoNome = findViewById(R.id.nomeUser);
        campoEndereco = findViewById(R.id.enderecoUser);
    }
}
