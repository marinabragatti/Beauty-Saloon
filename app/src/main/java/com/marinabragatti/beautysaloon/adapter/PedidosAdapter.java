package com.marinabragatti.beautysaloon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.model.ItemPedido;
import com.marinabragatti.beautysaloon.model.Pedidos;

import java.util.ArrayList;
import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.MyViewHolder> {

    private List<Pedidos> pedidosList;
    private Context context;

    public PedidosAdapter(List<Pedidos> pedidosList, Context context) {
        this.pedidosList = pedidosList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pedidos_adapter_lista, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pedidos pedidos = pedidosList.get(position);
        holder.nomeCliente.setText(pedidos.getNomeUser());

        int metodoPagto = pedidos.getMetodoPagto();
        String pagamento = metodoPagto == 0 ? "Dinheiro" : "Cartão";
        holder.formaPagto.setText("Forma pagto: " + pagamento);

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedidos.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for(ItemPedido itemPedido: itens){
            int qtde  = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            total += qtde * preco;

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + "- " + nome + " / (" + qtde + " x R$" + preco + ") \n";
            numeroItem ++;
        }
        descricaoItens +="Total: R$ " + total;
        holder.produto.setText(descricaoItens);
    }

    @Override
    public int getItemCount() {
        return pedidosList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //itens que a view ieá exibir
        TextView nomeCliente, formaPagto, produto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCliente = itemView.findViewById(R.id.textNomeCliente);
            formaPagto = itemView.findViewById(R.id.textFormaPagto);
            produto = itemView.findViewById(R.id.textProdutosPedidos);
        }
    }
}
