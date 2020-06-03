package com.marinabragatti.beautysaloon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServicosAdapter extends RecyclerView.Adapter<ServicosAdapter.MyViewHolder> {

    private List<Produto> produtoList;
    private Context context;

    public ServicosAdapter(List<Produto> produtoList, Context context) {
        this.produtoList = produtoList;
        this.context = context;
    }

    @NonNull
    @Override //Método chamado para criar as visualizações que aparecem na tela do user
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater converte um XML em view
        //from parent recupera o contexto do parent que o itemLista está dentro
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.empresa_adapter_lista, parent, false);
        return new MyViewHolder(itemList);
    }

    //Este método irá chamar o onBindViewHolder o número de vezes do seu retorno.
    //Desta forma a position vai sendo incrementada de acordo com as chamadas
    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    @Override //Quando o onCreate termina de criar as views, este método é chamado para exibir os elementos
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position); //Pego a posição para exibir o item correspondente a ela
        holder.nomeServico.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.valor.setText("R$" + produto.getValor());

        //Utilizo a biblioteca Picasso para o upload da foto cadastrada
        if(!produto.getUrlServico().isEmpty()){
            Picasso.get()
                    .load(produto.getUrlServico())
                    .into(holder.imgServico);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //itens que a view ieá exibir
        TextView nomeServico, descricao, valor;
        CircleImageView imgServico;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeServico = itemView.findViewById(R.id.textNome);
            descricao = itemView.findViewById(R.id.textDescricao);
            valor = itemView.findViewById(R.id.textValor);
            imgServico = itemView.findViewById(R.id.imageServico);
        }
    }
}
