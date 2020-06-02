package com.marinabragatti.beautysaloon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.adapter.ServicosAdapter;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.marinabragatti.beautysaloon.helper.UsuarioFirebase;
import com.marinabragatti.beautysaloon.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private ServicosAdapter servicosAdapter;
    private List<Produto> produtoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference firebaseRef;
    private String idUserLogado;
    private ValueEventListener valueEventListenerProdutos;
    private DatabaseReference produtosReferencia;
    private Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        autenticacao = ConfigFirebase.getFirebaseAuth();
        firebaseRef = ConfigFirebase.getFirebase();
        idUserLogado = UsuarioFirebase.getIdUser();
        recyclerView = findViewById(R.id.recyclerProducts);

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Beauty Saloon - Empresa");
        setSupportActionBar(toolbar);

        //Configurar Adapter
        servicosAdapter = new ServicosAdapter(produtoList, this);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this); //Tipo de layout que eu quero
        recyclerView.setLayoutManager(layoutManager); //set do layout escolhido acima
        recyclerView.setHasFixedSize(true); //set de layout fixo por recomendação do Google
        recyclerView.setAdapter(servicosAdapter);

        recuperarProdutos();
        swipe();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuSair :
                deslogarUser();
                break;
            case R.id.menuConfig :
                abrirConfig();
                break;
            case R.id.menuNovoProduto :
                addNovoProduto();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUser(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfig(){
        startActivity(new Intent(EmpresaActivity.this, ConfigEmpresaActivity.class));
    }

    private void addNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }

    public void recuperarProdutos() {
        produtosReferencia = firebaseRef.child("produtos").child(idUserLogado);//Recupera referência da empresa logada
        //O objeto valueEventListener terá acesso ao eventoListener para que possa parar de chamar dados no firebase no OnStop
        valueEventListenerProdutos = produtosReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//Com dataSnapshot recupero todos os serviços do usuário logado
                produtoList.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren())//getChildren percorre cada uma das movimentações com tudo que há dentro dela
                {
                    Log.i("PRODUTOS", "lista dos dados " + dados.getValue());
                    produtoList.add(dados.getValue(Produto.class));
                }
                servicosAdapter.notifyDataSetChanged();//notifica o adapter das mudanças na lista
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void swipe(){
        ItemTouchHelper.Callback itCallback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder) {
                int draFlags = ItemTouchHelper.ACTION_STATE_IDLE; //Deixar eventos drag and drop inativos
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; //Permite o swipe da esquerda para direita e vice versa
                return makeMovementFlags(draFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirServico(viewHolder); //ViewHolder recupera a posição do item da lista
            }
        };

        new ItemTouchHelper(itCallback).attachToRecyclerView(recyclerView); //Adiciona o swipe no recycler view
    }

    public void excluirServico(final RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Configura AlertDialog
        alertDialog.setTitle("Excluir Serviço/Produto");
        alertDialog.setMessage("Você tem certeza que deseja excluir este serviço?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition(); //Recupera a posição do item deslisado
                produto = produtoList.get(position); //Recupera o objeto produto/serviço

                produtosReferencia = firebaseRef.child("produtos")
                        .child(idUserLogado);//Acesso aos serviços daquele usuario no firebase

                produtosReferencia.child(produto.getIdProduto()).removeValue(); //Remove item da lista no firebase
                servicosAdapter.notifyItemRemoved(position);//Atualiza lista sem item removido
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(EmpresaActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                servicosAdapter.notifyDataSetChanged(); //Manter o item na lista após cancelar a caixa de dialog
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Override //Quando o usuário entrar no app o resumo será chamado no firebase
    protected void onStart() {
        super.onStart();
        recuperarProdutos();
    }

    @Override //Quando o app não estiver sendo utilizado, o firebase não será chamado, o listener é removido
    protected void onStop() {
        super.onStop();
        produtosReferencia.removeEventListener(valueEventListenerProdutos);
    }

}
