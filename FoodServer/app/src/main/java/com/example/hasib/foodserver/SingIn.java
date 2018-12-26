package com.example.hasib.foodserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class SingIn extends AppCompatActivity {
    FButton SingInBt;
    MaterialEditText number,password;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        SingInBt=(FButton)findViewById(R.id.buttonSingIn);
        number=(MaterialEditText) findViewById(R.id.Mnumber);
        password=(MaterialEditText) findViewById(R.id.MPassword);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference("User");


        SingInBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singInUser(number.getText().toString(),password.getText().toString());
            }
        });

    }

    private void singInUser(final String number, final String password) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              final  String local_number =number;
               final  String local_password = password;

                if (dataSnapshot.child(local_number).exists()) {
                    User user = dataSnapshot.child(local_number).getValue(User.class);
                    user.setPhone(local_number);

                    if (Boolean.parseBoolean(user.getIsStaff())){
                        if(user.getPassword().equals(local_password)){
                              Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_SHORT).show();

                            Intent in=new Intent(SingIn.this, Home.class);
                            Common.currentUser=user;
                            startActivity(in);

                            ///do something

                        }else
                            Toast.makeText(getApplicationContext(),"Password Wrong",Toast.LENGTH_SHORT).show();

                        }else
                        Toast.makeText(getApplicationContext(),"Log In with Staff Acount",Toast.LENGTH_SHORT).show();

                  }else {
                    Toast.makeText(getApplicationContext(),"User is not Exit in Database",Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
