package com.cse.cou.mobarak.dailybudget;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.cse.cou.mobarak.dailybudget.model.UserModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Registration extends AppCompatActivity {
    Button sign_up;
    EditText name, password, email;
    RadioGroup radioGroup;
    RadioButton gender;
    FirebaseAuth auth;
    Spinner spinner;
    int index;
    ProgressDialog progressBar;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        sharedPreferences=getSharedPreferences("cou.cse.mobarak.dailybudget",MODE_PRIVATE);

        MobileAds.initialize(this, "ca-app-pub-6222891704841980~8647952887");

        AdView adView= (AdView)findViewById(R.id.add_register);
        AdRequest adRequest=new AdRequest.Builder().build();

        adView.loadAd(adRequest);
        radioGroup = (RadioGroup) findViewById(R.id.gender);

        spinner= (Spinner) findViewById(R.id.currency_auto_complete_text);

        final String[] currency=getResources().getStringArray(R.array.currency);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,currency);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                index=i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setTitle("Registration Form");
        name = (EditText) findViewById(R.id.reg_name);
        password = (EditText) findViewById(R.id.reg_password);
        email = (EditText) findViewById(R.id.reg_email);

        auth = FirebaseAuth.getInstance();

        progressBar = new ProgressDialog(this);
        sign_up = (Button) findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString().trim();
                String p = password.getText().toString().trim();


                if(!e.isEmpty()&&!p.isEmpty()&&!name.getText().toString().isEmpty()){
                    progressBar.show();

                    auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser muser = auth.getCurrentUser();
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Registration");

                                int id = radioGroup.getCheckedRadioButtonId();
                                gender = (RadioButton) findViewById(id);
                                UserModel userModel = new UserModel(name.getText().toString(), email.getText().toString(), "",gender.getText().toString(),currency[index]);

                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("currency",currency[index]);
                                editor.commit();
                                DatabaseReference df = myRef.child(muser.getUid());
                                df.setValue(userModel);

                                progressBar.dismiss();
                                startActivity(new Intent(Registration.this, MainActivity.class));

                            } else {
                                progressBar.dismiss();

                                Toast.makeText(Registration.this, task.getException().getMessage() + " ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(Registration.this, "Enter all the fields", Toast.LENGTH_LONG).show();

                }


            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

    }


}
