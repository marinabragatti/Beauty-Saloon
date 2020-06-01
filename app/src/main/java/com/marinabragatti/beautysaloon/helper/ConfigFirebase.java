package com.marinabragatti.beautysaloon.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFirebase {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;
    private static StorageReference storageReference;


    //Retorna a referência do database
    public static DatabaseReference getFirebase(){
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    //Retorna a instância do FirebaseAuth
    public static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    //Retorna a referência do FirebaseStorage
    public static StorageReference getStorageReference(){
        if(storageReference == null){
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference;
    }
}
