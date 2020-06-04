package com.marinabragatti.beautysaloon.model;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;

import java.util.List;

public class Pedidos {

    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nomeUser;
    private List<ItemPedido> itens;
    private Double total;
    private String status = "pendente";
    private int metodoPagto;

    public Pedidos() {
    }

    public Pedidos(String idUser, String idEmpresa){
        setIdUsuario(idUser);
        setIdEmpresa(idEmpresa);

        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_user")
                .child(idUser)
                .child(idEmpresa);
        setIdPedido(pedidoRef.push().getKey());//Salvo o id do pedido
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_user")
                .child(getIdUsuario())
                .child(getIdEmpresa());
        pedidoRef.setValue(this);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagto() {
        return metodoPagto;
    }

    public void setMetodoPagto(int metodoPagto) {
        this.metodoPagto = metodoPagto;
    }
}
