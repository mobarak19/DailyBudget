package com.cse.cou.mobarak.dailybudget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowCostList extends AppCompatActivity {
    MyExpandableListAdapter myExpandableListAdapter;
    ExpandableListView expandableListView;
    FirebaseAuth auth;
    List<String> years;
    List<String> months;
    HashMap<String, List> hashMap;
    ProgressDialog progressDialog;

FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Year List");
        setContentView(R.layout.activity_show_cost_list);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingbutton_show_details);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShowCostList.this, AddBudget.class);
                intent.putExtra("monthly_budget","");
                intent.putExtra("daily_average","");
                startActivity(intent);
            }
        });

        years = new ArrayList<>();
        months = new ArrayList<>();
        hashMap = new HashMap<>();
        auth = FirebaseAuth.getInstance();
        String userid = auth.getCurrentUser().getUid();


        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        expandableListView = (ExpandableListView) findViewById(R.id.myexpandablelist);


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Registration").child(userid).child("cost");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dd : dataSnapshot.getChildren()) {
                        String year = dd.getKey();
                        years.add(year);
                        months.clear();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {

                            Map<String, Object> ma = (Map<String, Object>) d.getValue();
                            for (String m : ma.keySet()) {
                                months.add(m);
                            }
                        }
                        hashMap.put(year, months);

                    }

                    progressDialog.dismiss();

                    myExpandableListAdapter = new MyExpandableListAdapter(ShowCostList.this, years, hashMap);
                    expandableListView.setAdapter(myExpandableListAdapter);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();

                }
            });



    }

    @Override
    protected void onStart() {
        super.onStart();


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(ShowCostList.this, MainActivity.class));
        }
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
            startActivity(new Intent(ShowCostList.this, MainActivity.class));


        }
        return true;

    }


    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        String head, child;
        List<String> years;
        HashMap<String, List> hashMap;
        Context context;
        public MyExpandableListAdapter(Context context, List<String> years, HashMap<String, List> hashMap) {
            this.context = context;
            this.hashMap = hashMap;
            this.years = years;
        }
        @Override
        public int getGroupCount() {
            return years.size();
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            return hashMap.get(years.get(groupPosition)).size();
        }
        @Override
        public Object getGroup(int groupPosition) {
            return years.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return hashMap.get(years.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String header_title = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.headerlist, null);
            }
            final TextView textView = (TextView) convertView.findViewById(R.id.headerlist_);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(header_title);

            head = header_title;
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String children_string = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.childrenlist, null);
            }
            final TextView textView = (TextView) convertView.findViewById(R.id.childernlist_);
            textView.setText(children_string);


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    child = textView.getText().toString();
                    Intent intent = new Intent(context, CostDetails.class);
                    intent.putExtra("head", head);
                    intent.putExtra("child", child);
                    context.startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
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
