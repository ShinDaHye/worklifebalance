package com.example.dahye.wlb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    SignInButton Google_Login;
    TextView testview;
    Button Logout, Back;

    private static final int RC_SIGN_IN = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        testview = (TextView) findViewById(R.id.textView);
        Logout = (Button) findViewById(R.id.button);
        Back = (Button) findViewById(R.id.back);
        Back.setOnClickListener(this);
        Google_Login = findViewById(R.id.Google_Login);

        if(id != null){
            Logout.setVisibility(View.VISIBLE);
            Google_Login.setVisibility(View.GONE);
            testview.setText(id);
        }else{
            Logout.setVisibility(View.GONE);
            Google_Login.setVisibility(View.VISIBLE);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mAuth = FirebaseAuth.getInstance();

        Google_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                FirebaseUser user = mAuth.getCurrentUser();
                testview.setText((CharSequence) user);
                Toast.makeText(login.this,"Logout", Toast.LENGTH_LONG).show();
                Google_Login.setVisibility(View.VISIBLE);
                Logout.setVisibility(View.GONE);
//                testview.setText("");
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{
                //구글 로그인 실패
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            String providerId = user.getEmail();
                            testview.setText(providerId);
                            Google_Login.setVisibility(View.GONE);
                            Logout.setVisibility(View.VISIBLE);
                            Toast.makeText(login.this, "구글로그인 인증", Toast.LENGTH_SHORT).show();
                        }else{ // login fail
                            Toast.makeText(getApplicationContext(),"Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.back){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
