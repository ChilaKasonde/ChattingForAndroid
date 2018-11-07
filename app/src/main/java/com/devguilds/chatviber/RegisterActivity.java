package com.devguilds.chatviber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends ActionBarActivity {
    private EditText name,email, password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText)findViewById(R.id.editUsername);
        email = (EditText)findViewById(R.id.editEmail);
        password = (EditText)findViewById(R.id.editPassword);
        //get Authentication instance
        mAuth = FirebaseAuth.getInstance();
        //we pull the Users reference witiin our apps database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void signupButtonClick (View view){
        final String name_content, password_content, email_content;
        name_content = name.getText().toString().trim();
        email_content = email.getText().toString().trim();
        password_content = password.getText().toString().trim();
        if(!TextUtils.isEmpty(email_content) && !TextUtils.isEmpty(name_content) && !TextUtils.isEmpty(password_content) ){
           //if not empty attempts to create our user with an oncomplete listener that pushes the details to our database
            mAuth.createUserWithEmailAndPassword(email_content, password_content).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> authResultTask) {
                   if(authResultTask.isSuccessful()){
                       //we create a string to hold our uid
                       String user_id = mAuth.getCurrentUser().getUid();
                       //now we attempt to pull an actual user reference within the database using our uid
                       DatabaseReference current_user_db = mDatabase.child(user_id);
                       //now that we have a user reference we go further by applying the name_content/ password_content to the user in the DB
                       current_user_db.child("Name").setValue(name_content);
                       current_user_db.child("Password").setValue(password_content);

                       startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                   }
               }
           });
        }
    }

    public void loginButtonClicked(View view){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }



}
