package com.cse.cou.mobarak.dailybudget;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText email_link;
    Button sent_btn;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("Reset Password");

        MobileAds.initialize(this, "ca-app-pub-6222891704841980~8647952887");

        AdView adView= (AdView)findViewById(R.id.add_forgot_passwrd);
        AdRequest adRequest=new AdRequest.Builder().build();

        adView.loadAd(adRequest);
        email_link= (EditText) findViewById(R.id.reset_email);
        sent_btn= (Button) findViewById(R.id.reset_password);
        auth=FirebaseAuth.getInstance();
        sent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=email_link.getText().toString();
                if(!TextUtils.isEmpty(email)){
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(ForgotPassword.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(ForgotPassword.this," Reset link is sent. Cheak your email address",Toast.LENGTH_LONG).show();

                                startActivity(new Intent(ForgotPassword.this,MainActivity.class));
                                ForgotPassword.this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                            }else {
                                Toast.makeText(ForgotPassword.this,task.getException().getMessage()+"",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(ForgotPassword.this,"Please enter a valid password",Toast.LENGTH_LONG).show();
                }


            }
        });


    }
}
