package com.marinabragatti.beautysaloon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main); Não utilizo este layout qdo uso a biblioteca de Slides

        //Deixar os botões back e next invisíveis
        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_one)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_two)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_three)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_criar_conta)
                .canGoForward(false)
                .build());
    }

    // OnStart é chamado quando a Activity de Cadastro é fechada e voltamos para a MainActivity(que já executou o OnCreate anteriormente.
    // O método verificarUsuarioLogado é chamado, então o método abrirTelaPrincipal é chamado, que finalmente dá start em uma nova com.marinabragatti.beautysaloon.activity, PrincipalActivity.
    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    public void buttonCadastrar(View view){
        startActivity(new Intent(this, AutenticacaoActivity.class));
    }

    public void verificarUsuarioLogado(){
        autenticacao = ConfigFirebase.getFirebaseAuth();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        //autenticacao.signOut();
        //Verifica se o usuário está logado
        if(usuarioAtual != null){
            String tipoUser = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUser);
        }
    }

    public void abrirTelaPrincipal(String tipo){
        if(tipo.equals("U")){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class)); //Envio o usuário para home
        }else{
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class)); //Envio o usuário para homeEmpresa
        }
    }
}
