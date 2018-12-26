package com.example.dcl.foodshipperapplicaation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dcl.foodshipperapplicaation.Common.Common;
import com.example.dcl.foodshipperapplicaation.Model.Shipper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    EditText shipperName,shipperNumber,shipperPassword;
    Button shipperLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        shipperNumber=(EditText) findViewById(R.id.getName);
        shipperPassword=(EditText) findViewById(R.id.getPassword) ;
        shipperLogIn=(Button) findViewById(R.id.logInShipper);

        firebaseDatabase=FirebaseDatabase.getInstance();

        databaseReference=firebaseDatabase.getReference("Shipper");

        shipperLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn(shipperNumber.getText().toString(),shipperPassword.getText().toString());
            }
        });
    }

    private void logIn(final String number, final String password) {

        databaseReference.child(number).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Shipper shipper=dataSnapshot.getValue(Shipper.class);


                            if (shipper.getPassword().equals(password)){

                                startActivity(new Intent(MainActivity.this,HomeActivity.class));

                                Common.currentShipper=shipper;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }
}
