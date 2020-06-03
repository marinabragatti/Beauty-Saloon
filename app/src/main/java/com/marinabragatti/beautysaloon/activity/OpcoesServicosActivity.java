package com.marinabragatti.beautysaloon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.adapter.ServicosAdapter;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.marinabragatti.beautysaloon.model.Empresa;
import com.marinabragatti.beautysaloon.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpcoesServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CircleImageView imageEmpresa;
    private TextView nomeEmpresa;
    private Empresa empresaSelecionada;
    private ServicosAdapter servicosAdapter;
    private List<Produto> produtoList = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private ValueEventListener valueEventListenerProdutos;
    private DatabaseReference produtosReferencia;
    private String idEmpresa;

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

        recuperarEmpresaSelecionada();

        recuperarProdutos();

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

    public void recuperarProdutos() {
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
