package com.marinabragatti.beautysaloon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.marinabragatti.beautysaloon.helper.UsuarioFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoEntrar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUser;
    private LinearLayout tipoUserContainer;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        incicializarComponentes();
        autenticacao = ConfigFirebase.getFirebaseAuth();

        //Mostra o switch de usuário/empresa caso seja um cadastro novo
        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tipoUserContainer.setVisibility(View.VISIBLE);
                }else{
                    tipoUserContainer.setVisibility(View.GONE);
                }

            }
        });

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if(!email.isEmpty()){
                    if(!senha.isEmpty()){
                        //Verificar o estado do switch
                        if(tipoAcesso.isChecked()){ //Cadastro
                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){ //Verifica se o cadastro deu certo
                                        Toast.makeText(AutenticacaoActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = getTipoUser();
                                        UsuarioFirebase.atualizarTipoUser(tipoUsuario);
                                        abrirTelaHome(tipoUsuario);
                                    }else{
                                        String exception = "";
                                        try{
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            exception = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            exception = "Digite um email válido!";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            exception = "Usuário já cadastrado!";
                                        }catch(Exception e){
                                            exception = "Erro ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace(); //Mostra o erro exato
                                        }
                                        Toast.makeText(AutenticacaoActivity.this, exception, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{ //Login
                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) { //Verifica se o login deu certo
                                        Toast.makeText(AutenticacaoActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = task.getResult().getUser().getDisplayName();
                                        UsuarioFirebase.atualizarTipoUser(tipoUsuario);
                                        abrirTelaHome(tipoUsuario);
                                    }else{
                                        Toast.makeText(AutenticacaoActivity.this, "Erro ao fazer o login", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }else{
                        Toast.makeText(AutenticacaoActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AutenticacaoActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void incicializarComponentes(){
        botaoEntrar = findViewById(R.id.buttonEntrar);
        campoEmail = findViewById(R.id.email);
        campoSenha = findViewById(R.id.password);
        tipoAcesso = findViewById(R.id.accessType);
        tipoUser = findViewById(R.id.userType);
        tipoUserContainer = findViewById(R.id.switchTypeUser);
    }

    private void abrirTelaHome(String tipo){
        if(tipo.equals("U")){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class)); //Envio o usuário para home
        }else{
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class)); //Envio o usuário para homeEmpresa
        }
    }

    private String getTipoUser(){
        return tipoUser.isChecked() ? "E" : "U";
    }
}
