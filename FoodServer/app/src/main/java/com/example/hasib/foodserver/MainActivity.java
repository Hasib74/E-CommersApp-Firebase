package com.example.hasib.foodserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    FButton singInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singInButton=(FButton)findViewById(R.id.btsingIn);
        singInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MainActivity.this,SingIn.class);
                startActivity(in);
            }
        });
    }
}
