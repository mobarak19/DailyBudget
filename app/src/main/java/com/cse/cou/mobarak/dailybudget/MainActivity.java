package com.cse.cou.mobarak.dailybudget;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    Button login_btn, register_btn;
    FirebaseAuth firebaseAuth;
    EditText email, password;
    String e, p;
    TextView forgot_pss;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_btn = (Button) findViewById(R.id.sign_in);
        firebaseAuth = FirebaseAuth.getInstance();

        MobileAds.initialize(this, "ca-app-pub-6222891704841980~8647952887");

        AdView adView= (AdView)findViewById(R.id.add_mainactivity);
        AdRequest adRequest=new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        forgot_pss= (TextView) findViewById(R.id.forgot_pass_text);
        forgot_pss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,ForgotPassword.class));
                MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            email = (EditText) findViewById(R.id.login_email);
                password = (EditText) findViewById(R.id.login_password);
                e = email.getText().toString().trim();
                p = password.getText().toString().trim();

                if(!e.isEmpty()&&!p.isEmpty()){
                    progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(e, p).
                            addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(MainActivity.this, ShowCostList.class));
                                    } else {
                                        progressDialog.dismiss();

                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }


                                }
                            });
                }else {
                    Toast.makeText(MainActivity.this, "Enter email and password", Toast.LENGTH_LONG).show();

                }


            }
        });


        register_btn = (Button) findViewById(R.id.register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,ShowCostList.class));

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
