package com.marinabragatti.beautysaloon.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    //Recupera o id do user logado
    public static String getIdUser(){
        FirebaseAuth autenticacao = ConfigFirebase.getFirebaseAuth();
        return autenticacao.getCurrentUser().getUid();
    }

    //Recupera o user logado
    public static FirebaseUser getUserAtual(){
        FirebaseAuth usuario = ConfigFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarTipoUser(String tipo){
        try{
            FirebaseUser user = getUserAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo) //Método utilizado para set do userName, mas neste caso estou salvando o tipo User/Empresa
                    .build();
            user.updateProfile(profile);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
