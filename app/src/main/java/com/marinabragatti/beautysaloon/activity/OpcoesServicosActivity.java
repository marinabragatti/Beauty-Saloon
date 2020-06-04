package com.marinabragatti.beautysaloon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.adapter.ServicosAdapter;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.marinabragatti.beautysaloon.helper.UsuarioFirebase;
import com.marinabragatti.beautysaloon.listener.RecyclerItemClickListener;
import com.marinabragatti.beautysaloon.model.Empresa;
import com.marinabragatti.beautysaloon.model.ItemPedido;
import com.marinabragatti.beautysaloon.model.Pedidos;
import com.marinabragatti.beautysaloon.model.Produto;
import com.marinabragatti.beautysaloon.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class OpcoesServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CircleImageView imageEmpresa;
    private TextView nomeEmpresa;
    private Empresa empresaSelecionada;
    private Pedidos pedidoRecuperado;
    private Usuario usuario;
    private ServicosAdapter servicosAdapter;
    private List<Produto> produtoList = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private ValueEventListener valueEventListenerProdutos;
    private DatabaseReference produtosReferencia;
    private String idEmpresa;
    private AlertDialog dialog;
    private String idUserLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcoes_servicos);

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Serviços");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseRef = ConfigFirebase.getFirebase();
        idUserLogado = UsuarioFirebase.getIdUser();//Recupera user id
        recyclerView = findViewById(R.id.recyclerOpcoesServico);
        imageEmpresa = findViewById(R.id.imagemEmpresa);
        nomeEmpresa = findViewById(R.id.textNome);

        //Configurar Adapter
        servicosAdapter = new ServicosAdapter(produtoList, this);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this); //Tipo de layout que eu quero
        recyclerView.setLayoutManager(layoutManager); //set do layout escolhido acima
        recyclerView.setHasFixedSize(true); //set de layout fixo por recomendação do Google
        recyclerView.setAdapter(servicosAdapter);

        //Configura evento de clique no recycler
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                confirmarQtde(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        recuperarEmpresaSelecionada();

        recuperarProdutos();

        recuperarDadosUser();

    }

    private void recuperarEmpresaSelecionada(){
        //Recuperar empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");//colocar em key o nome do envio no intent.putExtra da HomeActivity
            nomeEmpresa.setText(empresaSelecionada.getNome());
            idEmpresa = empresaSelecionada.getIdUsuario();
            if(!empresaSelecionada.getUrlImagem().isEmpty()){
                Picasso.get()
                        .load(empresaSelecionada.getUrlImagem())
                        .into(imageEmpresa);
            }
        }
    }

    private void recuperarProdutos() {
        produtosReferencia = firebaseRef.child("produtos").child(idEmpresa);//Recupera referência da empresa logada
        //O objeto valueEventListener terá acesso ao eventoListener para que possa parar de chamar dados no firebase no OnStop
        valueEventListenerProdutos = produtosReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//Com dataSnapshot recupero todos os serviços do usuário logado
                produtoList.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren())//getChildren percorre cada um dos serviços com tudo que há dentro dele
                {
                    produtoList.add(dados.getValue(Produto.class));
                }
                servicosAdapter.notifyDataSetChanged();//notifica o adapter das mudanças na lista
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperarDadosUser(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference userRef = firebaseRef
                .child("usuarios")
                .child(idUserLogado);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    usuario = dataSnapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperarPedido(){
        dialog.dismiss();
    }

    private void confirmarQtde(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQtde = new EditText(this);
        editQtde.setInputType(InputType.TYPE_CLASS_NUMBER);
        editQtde.setText("1");

        builder.setView(editQtde);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantidade = editQtde.getText().toString();
                Produto produtoSelecionado = produtoList.get(position);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getValor());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));
                int verifica = 0;

                if(itensCarrinho.isEmpty()){
                    itensCarrinho.add(itemPedido);
                }else{
                    for(int i = 0; i<itensCarrinho.size(); i++){
                        // Verifica se o produto ja existe no carrinho
                        if( (itensCarrinho.get(i).getIdProduto().equals( produtoSelecionado.getIdProduto() ) )) {
                            itemPedido.setQuantidade(itemPedido.getQuantidade() + Integer.parseInt(quantidade));
                        }else{
                            verifica = i+1;
                        }
                    }
                    if(verifica == itensCarrinho.size()){
                        itensCarrinho.add( itemPedido );
                    }
                }
                if(pedidoRecuperado == null){
                    pedidoRecuperado = new Pedidos(idUserLogado, idEmpresa); //instancio e já seto um id para o pedido no construtor de Pedidos.class
                    pedidoRecuperado.setNomeUser(usuario.getNome());
                }

                pedidoRecuperado.setItens(itensCarrinho);
                pedidoRecuperado.salvar();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override //Quando o usuário entrar no app o resumo será chamado no firebase
    protected void onStart() {
        super.onStart();
        recuperarProdutos();
    }

    @Override //Quando o app não estiver sendo utilizado, o firebase não será chamado, o com.marinabragatti.beautysaloon.listener é removido
    protected void onStop() {
        super.onStop();
        produtosReferencia.removeEventListener(valueEventListenerProdutos);
    }
}
