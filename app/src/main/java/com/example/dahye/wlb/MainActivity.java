package com.example.dahye.wlb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button login, intent_add,intent_tuto, intent_graph;
    TextView providerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.login);
        intent_add = (Button) findViewById(R.id.intent_add);
        intent_tuto = (Button) findViewById(R.id.intent_tuto);
        intent_graph = (Button)findViewById(R.id.intent_graph);
        providerId = (TextView) findViewById(R.id.providerId);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            providerId.setText(user.getEmail());
        }else{
            providerId.setText("");
        }
        if(providerId.getText()==""){
            intent_add.setVisibility(View.INVISIBLE);
        }
        login.setOnClickListener(this);
        intent_add.setOnClickListener(this);
        intent_tuto.setOnClickListener(this);
        intent_graph.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.login){
            Intent intent = new Intent(this,login.class);
            if(providerId.getText() != ""){
                intent.putExtra("id",providerId.getText().toString());
            }
            startActivity(intent);
        }else if(view.getId()==R.id.intent_add){
            Intent intent = new Intent(this,addcategory.class);
            startActivity(intent);
        }else if(view.getId()==R.id.intent_tuto){
            Intent intent = new Intent(this,addcategory.class);
            startActivity(intent);
        }else if(view.getId()==R.id.intent_graph){
            Intent intent = new Intent(this,graph.class);
            startActivity(intent);
        }else{
            finish();
        }
    }

}
