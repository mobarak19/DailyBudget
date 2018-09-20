package com.cse.cou.mobarak.dailybudget;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddBudget extends AppCompatActivity {

    EditText cost, amount;
    int date, cost_amount, year;
    String current_month, cost_name;
    Button save_cost, goto_costList;
    TextView current_time;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    Button setdate;
    EditText setMonthlyBudget;
    TextView setDailyAverage;
    SharedPreferences sharedPreferences;
    String currency;
    TextView textView, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
        setTitle("Add Cost Details");
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences("cou.cse.mobarak.dailybudget",MODE_PRIVATE);

        currency=sharedPreferences.getString("currency","");

        textView= (TextView) findViewById(R.id.currency_add_budget);
        textView.setText(currency+"");
        textView2= (TextView) findViewById(R.id.currency_add_budget2);
        textView2.setText(currency+"");
        MobileAds.initialize(this, "ca-app-pub-6222891704841980~8647952887");

        AdView adView = (AdView) findViewById(R.id.add_add_budget);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

 current_time = (TextView) findViewById(R.id.current_date);


        cost = (EditText) findViewById(R.id.cost_reason);
        save_cost = (Button) findViewById(R.id.save_cost);
        amount = (EditText) findViewById(R.id.cost_amount);
        final String[] month = {"January", "February", "March", "April", "May"
                , "June", "July", "August", "September",
                "October", "November", "December"};

        Calendar calendar = Calendar.getInstance();

        current_month = month[calendar.get(Calendar.MONTH)];
        date = calendar.get(Calendar.DATE);

        year = calendar.get(Calendar.YEAR);


        current_time.setText("Today is : " + year + "-" + current_month + "-" + date);

        setMonthlyBudget= (EditText) findViewById(R.id.set_monthly_budget);
        setMonthlyBudget.setText(getIntent().getIntExtra("monthly_budget",0)+"");


        setDailyAverage= (TextView) findViewById(R.id.show_daily_average_budget);
        setDailyAverage.setText("Expected Daily Average: "+getIntent().getIntExtra("daily_average",0)+currency);

        setdate = (Button) findViewById(R.id.setdate_btn);
        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Calendar myCalendar1 = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar1.set(Calendar.YEAR, year);
                        myCalendar1.set(Calendar.MONTH, monthOfYear);
                        myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        year=myCalendar1.get(Calendar.YEAR);
                        current_month=month[myCalendar1.get(Calendar.MONTH)];
                        date=myCalendar1.get(Calendar.DATE);
                        current_time.setText("Today is : " + year + "-" + current_month + "-" + date);

                    }

                };

                new DatePickerDialog(AddBudget.this, date1, myCalendar1
                        .get(Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        goto_costList = (Button) findViewById(R.id.goto_costList);
        goto_costList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddBudget.this, ShowCostList.class));
            }
        });
        progressDialog = new ProgressDialog(this);

        save_cost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                cost_name = cost.getText().toString();
                cost_amount = 0;
                if (!amount.getText().toString().isEmpty()) {
                    cost_amount = Integer.parseInt(amount.getText().toString());
                }

                if (!cost_name.isEmpty() && cost_amount != 0&&!setMonthlyBudget.getText().toString().isEmpty()) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("Registration");
                    DatabaseReference df = databaseReference.child(firebaseAuth.getCurrentUser().getUid());
                    DatabaseReference costdf = df.child("cost");

                    DatabaseReference current_year = costdf.child(year + "");
                    DatabaseReference monthref = current_year.child(current_month);

                    monthref.child("budget").setValue(setMonthlyBudget.getText().toString().trim());

                    DatabaseReference day_ref = monthref.child(date + "");
                    Map<String, Integer> map = new HashMap<>();
                    map.put(cost_name, cost_amount);
                    day_ref.push().setValue(map);

                    progressDialog.dismiss();
                    cost.setText("");
                    amount.setText("");
                } else {
                    progressDialog.dismiss();

                    Toast.makeText(AddBudget.this, "Enter cost reason, amount and monthly budget.", Toast.LENGTH_LONG).show();

                }


            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = R.id.logout;
        if (id == item.getItemId()) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AddBudget.this, MainActivity.class));


        }
        return true;

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(AddBudget.this, MainActivity.class));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddBudget.this, ShowCostList.class));
        finish();
    }


}
