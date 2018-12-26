package com.example.dcl.foodshipperapplicaation.Service;


import com.example.dcl.foodshipperapplicaation.Common.Common;
import com.example.dcl.foodshipperapplicaation.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by HASIB on 6/29/2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        sendTokenToServer(refreshedToken);
    }



    private void sendTokenToServer(String refreshedToken) {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference referance=db.getReference("Token");
        Token token=new Token(refreshedToken,true);
        referance.child(Common.currentShipper.getNumber()).setValue(token);

    }
}
