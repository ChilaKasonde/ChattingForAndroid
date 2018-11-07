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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends ActionBarActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private FirebaseAuth lAuth;
    private DatabaseReference lDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginEmail = (EditText)findViewById(R.id.logEmail);
        loginPassword = (EditText)findViewById(R.id.logPassword);
        //we create an auth reference
        lAuth = FirebaseAuth.getInstance();
        //database instance with the Users
        lDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void logingButtonClicked(View view){
        String email = loginEmail.getText().toString().trim();
        String  pass = loginPassword.getText().toString().trim();
        
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
        //attempts to sign in with email and pass
            lAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> authResultTask) {
                    if(authResultTask.isSuccessful()){
                        //if successful sends calls this method for existence check if user exists
                        checkUserExists();
                    }
                }
        });
    }
}
    public void checkUserExists(){
        //gets current users UID
        final String user_id = lAuth.getCurrentUser().getUid();
        lDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //checks if database has a child with that uid
                if (dataSnapshot.hasChild(user_id)) {
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}