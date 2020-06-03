package com.marinabragatti.beautysaloon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.adapter.EmpresasAdapter;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.marinabragatti.beautysaloon.listener.RecyclerItemClickListener;
import com.marinabragatti.beautysaloon.model.Empresa;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private EmpresasAdapter empresasAdapter;
    private List<Empresa> empresaList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference firebaseRef;
    private ValueEventListener valueEventListenerEmpresas;
    private DatabaseReference empresasReferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        autenticacao = ConfigFirebase.getFirebaseAuth();
        firebaseRef = ConfigFirebase.getFirebase();
        searchView = findViewById(R.id.materialSearch);

        recyclerView = findViewById(R.id.recyclerEmpresas);

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Beauty Saloon");
        setSupportActionBar(toolbar);

        //Configurar Adapter
        empresasAdapter = new EmpresasAdapter(empresaList, this);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this); //Tipo de layout que eu quero
        recyclerView.setLayoutManager(layoutManager); //set do layout escolhido acima
        recyclerView.setHasFixedSize(true); //set de layout fixo por recomendação do Google
        recyclerView.setAdapter(empresasAdapter);

        recuperarEmpresas();

        //Configuração SearchView
        searchView.setHint("Pesquisar Salões");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarSalao(newText);
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Empresa empresaSelecionada = empresaList.get(position);
                        Intent intent = new Intent(HomeActivity.this, OpcoesServicosActivity.class);
                        //para enviar empresa selecionada abaixo é preciso colocar implements serializable na classe empresa
                        intent.putExtra("empresa", empresaSelecionada); //Envia os dados da empresa selecionada para a OpcoesActivity
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);

        //Configuração Menu Pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisar);
        searchView.setMenuItem(item);

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
        startActivity(new Intent(HomeActivity.this, ConfigUserActivity.class));
    }

    private void recuperarEmpresas(){
        empresasReferencia = firebaseRef.child("empresas");//Recupera referência das empresas cadastradas
        //O objeto valueEventListener terá acesso ao eventoListener para que possa parar de chamar dados no firebase no OnStop
        valueEventListenerEmpresas = empresasReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//Com dataSnapshot recupero todas as empresas disponíveis no firebase
                empresaList.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren())//getChildren percorre cada uma das empresas com tudo que há dentro delas
                {
                    empresaList.add(dados.getValue(Empresa.class));
                }
                empresasAdapter.notifyDataSetChanged();//notifica o adapter das mudanças na lista
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void pesquisarSalao(String text){
        DatabaseReference empresasRef = firebaseRef.child("empresas");
        Query query = empresasRef.orderByChild("nome")
                .startAt(text)
                .endAt(text + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                empresaList.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren())//getChildren percorre cada uma das empresas com tudo que há dentro delas
                {
                    empresaList.add(dados.getValue(Empresa.class));
                }
                empresasAdapter.notifyDataSetChanged();//notifica o adapter das mudanças na lista
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override //Quando o usuário entrar no app o resumo será chamado no firebase
    protected void onStart() {
        super.onStart();
        recuperarEmpresas();
    }

    @Override //Quando o app não estiver sendo utilizado, o firebase não será chamado, o com.marinabragatti.beautysaloon.listener é removido
    protected void onStop() {
        super.onStop();
        empresasReferencia.removeEventListener(valueEventListenerEmpresas);
    }

}
