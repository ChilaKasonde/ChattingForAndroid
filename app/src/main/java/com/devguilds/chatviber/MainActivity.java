package com.devguilds.chatviber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends ActionBarActivity {


    private EditText editMessage;
    private DatabaseReference mDatabase;
    private RecyclerView mMessageList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editMessage = (EditText)findViewById(R.id.MeditMessage);
        //creates database instance with a collection called Messages
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Messages");
        //creates a reference to the RecyclerView
        mMessageList = (RecyclerView)findViewById(R.id.messageRec);
        //we set a fixed size to avoid overlapping of views
        mMessageList.setHasFixedSize(true);
        //creates a linerlayout that we tie to the recylerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();

       mAuthListener = new FirebaseAuth.AuthStateListener() {
           @Override
           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
               }
           }
       };

    }

    public void sendButtonClick(View view){
        //grabs current logged in users instance
        mCurrentUser = mAuth.getCurrentUser();
        //we create another database refrence targeting the Users collection this time inorder to get current users Uid
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        final String messageValue = editMessage.getText().toString().trim();
        //checks if the edit text is empty
        if(!TextUtils.isEmpty(messageValue)){
            //creates a push instance that will allow to post to the dataabase
            final DatabaseReference newPost = mDatabase.push();
            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newPost.child("content").setValue(messageValue);
                    newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> voidTask) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mMessageList.scrollToPosition(mMessageList.getAdapter().getItemCount());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //creates a firebase instance of an adaptor
        FirebaseRecyclerAdapter<Message,MessageViewHolder> FBRA = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,R.layout.singlemessagelayout,MessageViewHolder.class,
                mDatabase) {
            @Override
            protected void populateViewHolder(MessageViewHolder MviewHolder, Message model, int i) {
                //grabs content and populates recyclerview
                MviewHolder.setContent(model.getContent());
                MviewHolder.setUsername(model.getUsername());
            }
        };
        //ties FirebaseREcyclerADaptor to our localRecyler
        mMessageList.setAdapter(FBRA);
    }


    //a nested class that extends our Recycler
    public static class MessageViewHolder extends RecyclerView.ViewHolder{
            View mView;
            public MessageViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
            }
        //applies text from the Database to a local view
        public  void setContent(String content){
                TextView message_content = (TextView)mView.findViewById(R.id.messageText);
                message_content.setText(content);
            }
        public void setUsername(String username){
            TextView username_content = (TextView)mView.findViewById(R.id.usernameText);
            username_content.setText(username);
        }
        }

}
