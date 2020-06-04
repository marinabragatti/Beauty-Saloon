package com.marinabragatti.beautysaloon.model;

import com.google.firebase.database.DatabaseReference;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;

public class Usuario {

    private String idUsuario;
    private String nome;
    private String endereco;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference userRef = firebaseRef.child("usuarios")
                .child(getIdUsuario());
        userRef.setValue(this);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
