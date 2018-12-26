package com.example.hasib.foodserver.Server;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by HASIB on 4/2/2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        
        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        sendNotification(refreshedToken);
    }

    private void sendNotification(String refreshedToken) {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference referance=db.getReference("Token");
        Token token=new Token(refreshedToken,true);
        referance.child(Common.currentUser.getPhone()).setValue(token);

    }
}
