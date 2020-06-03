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
import com.marinabragatti.beautysaloon.model.Empresa;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmpresasAdapter extends RecyclerView.Adapter<EmpresasAdapter.MyViewHolder> {

    private List<Empresa> empresaList;
    private Context context;

    public EmpresasAdapter(List<Empresa> empresaList, Context context) {
        this.empresaList = empresaList;
        this.context = context;
    }

    @NonNull
    @Override//Método chamado para criar as visualizações que aparecem na tela do user
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater converte um XML em view
        //from parent recupera o contexto do parent que o itemLista está dentro
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cliente_adapter_lista, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override//Quando o onCreate termina de criar as views, este método é chamado para exibir os elementos
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Empresa empresa = empresaList.get(position);
        holder.nomeEmpresa.setText(empresa.getNome());
        holder.endereco.setText(empresa.getEndereco());

        //Utilizo a biblioteca Picasso para o upload da foto cadastrada
        if(!empresa.getUrlImagem().isEmpty()){
            Picasso.get()
                    .load(empresa.getUrlImagem())
                    .into(holder.imgEmpresa);
        }
    }

    //Este método irá chamar o onBindViewHolder o número de vezes do seu retorno.
    //Desta forma a position vai sendo incrementada de acordo com as chamadas
    @Override
    public int getItemCount() {
        return empresaList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //itens que a view ieá exibir
        TextView nomeEmpresa, endereco;
        CircleImageView imgEmpresa;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeEmpresa = itemView.findViewById(R.id.textNome);
            endereco = itemView.findViewById(R.id.textEndereco);
            imgEmpresa = itemView.findViewById(R.id.imageEmpresa);
        }
    }
}
