package com.marinabragatti.beautysaloon.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.marinabragatti.beautysaloon.helper.UsuarioFirebase;
import com.marinabragatti.beautysaloon.model.Empresa;
import com.marinabragatti.beautysaloon.model.Produto;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText campoNome, campoDescricao;
    private CurrencyEditText campoValor;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUserLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        inicializarComponentes();
        storageReference = ConfigFirebase.getStorageReference();//Recupera referência do Storage
        idUserLogado = UsuarioFirebase.getIdUser();//Recupera user id
        firebaseRef = ConfigFirebase.getFirebase();//Recupera referência do firebase

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //abre intent para escolher foto
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });

    }

    //Verifica se a imagem foi carregada corretamente e a prepara para salvar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImage = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                        break;
                }
                if(imagem != null){
                    //configuração da imagem em Bitmap
                    imageView.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //referência para pasta no Storage
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("servicos")
                            .child(idUserLogado)
                            .child(imagem + "jpeg");

                    //upload dos bytes da imagem
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Erro ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Sucesso ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //recupera o link de download da imagem
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return imagemRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUri = task.getResult();

                                //salva a url na string
                                urlImagemSelecionada = downloadUri.toString();
                            }
                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void salvarNovoProduto(View view){

        if(validarCampos()){
            String nome = campoNome.getText().toString();
            String descricao = campoDescricao.getText().toString();
            Double valor = transformaDouble(campoValor.getRawValue());

            Produto produto = new Produto();
            produto.setIdUsuario(idUserLogado);
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setValor(valor);
            produto.setUrlServico(urlImagemSelecionada);
            produto.salvar();
            finish();
            exibirToast("Serviço salvo com sucesso!");
        }

    }

    public boolean validarCampos(){
        //Valida se os campos foram preenchidos
        String nome = campoNome.getText().toString();
        String descricao = campoDescricao.getText().toString();
        String valor = String.valueOf(campoValor.getRawValue());


        if(!nome.isEmpty()){
            if(!descricao.isEmpty()){
                if(!valor.isEmpty() && !valor.equals("0")){
                    return true;
                }else{
                    exibirToast("Preencha o valor do seu serviço");
                    return false;
                }
            }else{
                exibirToast("Preencha a descrição do seu serviço");
                return false;
            }
        }else{
            exibirToast("Preencha o nome do seu serviço");
            return false;
        }
    }

    public void exibirToast(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    public Double transformaDouble(long precoInt){
        Double valorDecimal = Long.valueOf(precoInt).doubleValue();
        valorDecimal /= 100;
        return valorDecimal;
    }


    public void inicializarComponentes(){
        imageView = findViewById(R.id.profile_image_servico);
        campoNome = findViewById(R.id.nomeServico);
        campoDescricao = findViewById(R.id.descricaoServico);
        campoValor = findViewById(R.id.valorServico);

        //Configura localidade para ptBR
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
    }

}
