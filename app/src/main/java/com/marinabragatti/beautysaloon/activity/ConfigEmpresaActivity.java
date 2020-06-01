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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marinabragatti.beautysaloon.R;
import com.marinabragatti.beautysaloon.helper.ConfigFirebase;
import com.marinabragatti.beautysaloon.helper.UsuarioFirebase;
import com.marinabragatti.beautysaloon.model.Empresa;

import java.io.ByteArrayOutputStream;

public class ConfigEmpresaActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText campoNome, campoEndereco;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String idUserLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_empresa);

        inicializarComponentes();
        storageReference = ConfigFirebase.getStorageReference();
        idUserLogado = UsuarioFirebase.getIdUser();

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
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
                    imageView.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUserLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigEmpresaActivity.this,
                                    "Erro ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            urlImagemSelecionada = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            Toast.makeText(ConfigEmpresaActivity.this,
                                    "Sucesso ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void validarDadosEmpresa(View view){
        //Valida se os campos foram preenchidos
        String nome = campoNome.getText().toString();
        String endereco = campoEndereco.getText().toString();

        if(!nome.isEmpty()){
            if(!endereco.isEmpty()){
                Empresa empresa = new Empresa();
                empresa.setIdUsuario(idUserLogado);
                empresa.setNome(nome);
                empresa.setEndereco(endereco);
                empresa.setUrlImagem(urlImagemSelecionada);
                empresa.salvar();
                finish();
            }else{
                exibirToast("Digite o endereço da sua empresa");
            }
        }else{
            exibirToast("Digite o nome da sua empresa");
        }
    }

    public void exibirToast(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT);
    }

    public void inicializarComponentes(){
        imageView = findViewById(R.id.profile_image_empresa);
        campoNome = findViewById(R.id.nomeEmpresa);
        campoEndereco = findViewById(R.id.enderecoEmpresa);
    }
}
