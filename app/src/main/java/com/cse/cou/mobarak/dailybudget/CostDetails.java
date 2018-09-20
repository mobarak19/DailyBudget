package com.cse.cou.mobarak.dailybudget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
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


public class CostDetails extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String currency;

    String year_str, month_str, total_cost,kk;

    int total,month_total=0,monthly_budget;
    MyExpandableListAdapter myExpandableListAdapter;
    ExpandableListView expandableListView;
    FirebaseAuth auth;
    ArrayList<String> date;
    HashMap<String, String> month_cost;
    HashMap<String, List> hashMap;
    HashMap<String, List> childKey;

    HashMap<String,Integer> month_map;
    FloatingActionButton floatingActionButton;
    FirebaseAuth firebaseAuth;
    int ave;
    TextView getMonthly_budget,getDaily_budget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_details);

        sharedPreferences=getSharedPreferences("cou.cse.mobarak.dailybudget",MODE_PRIVATE);

        currency=sharedPreferences.getString("currency","");

        auth = FirebaseAuth.getInstance();


        date = new ArrayList<>();
        month_map=new HashMap<>();
        month_map.put("January",31);
        month_map.put("February",28);
        month_map.put("March",31);
        month_map.put("April",30);
        month_map.put("May",31);
        month_map.put("June",30);
        month_map.put("July",31);
        month_map.put("August",31);
        month_map.put("September",30);
        month_map.put("October",31);
        month_map.put("November",30);
        month_map.put("December",31);
        getDaily_budget= (TextView) findViewById(R.id.getDaily_budget);
        getMonthly_budget= (TextView) findViewById(R.id.getMonthly_budget);



        childKey=new HashMap<>();


        total = 0;
        hashMap = new HashMap<>();
        auth = FirebaseAuth.getInstance();

        month_cost = new HashMap<>();
        expandableListView = (ExpandableListView) findViewById(R.id.list_of_date_cost);

        year_str = getIntent().getStringExtra("head");
        month_str = getIntent().getStringExtra("child");
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Registration").child(auth.getCurrentUser().getUid()).child("currency");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ;
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("currency",dataSnapshot.getValue().toString());
                editor.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Registration").child(auth.getCurrentUser().getUid()).
                child("cost").child(year_str).child(month_str);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data:dataSnapshot.getChildren()){
                    String m=data.getKey();
                    if(m.equals("budget")){
                        String str_budet=data.getValue().toString().trim();
                        monthly_budget=Integer.parseInt(str_budet);
                    }
                    date.add(data.getKey());
                    ArrayList<String> keyItem=new ArrayList<String>();
                    ArrayList<String> date_cost=new ArrayList<String>();
                    for (DataSnapshot d:data.getChildren()){

                        date_cost.add(d.getValue().toString());
                        keyItem.add(d.getKey());
                        String v = d.getValue().toString().replaceAll("[{}]", "");
                        String[] k = v.split("=");

                        total=total+Integer.parseInt(k[1]);

                    }
                    hashMap.put(m,date_cost);

                    childKey.put(m,keyItem);
                    month_cost.put(m, total+"");
                    month_total=month_total+total;

                    total=0;

                }




                LinearLayout linearLayout= (LinearLayout) findViewById(R.id.show_average);
                linearLayout.setVisibility(View.VISIBLE);
                    myExpandableListAdapter = new MyExpandableListAdapter(CostDetails.this, date, hashMap);
                    expandableListView.setAdapter(myExpandableListAdapter);
                getMonthly_budget.setText("Monthly Budget: "+monthly_budget+""+currency);
                setTitle(month_str+", Total Cost: "+month_total+currency);
                ave=monthly_budget/month_map.get(month_str);
                getDaily_budget.setText("Expected Daily Expenses: "+ave+currency);
                floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingbutton_cost_details);
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(CostDetails.this, AddBudget.class);
                        intent.putExtra("monthly_budget",monthly_budget);
                        intent.putExtra("daily_average",ave);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=R.id.logout;
        if(id==item.getItemId()){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(CostDetails.this,MainActivity.class));


        }
        return true;

    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        String head;
        List<String> date;
        HashMap<String, List> hashMap;
        Context context;

        public MyExpandableListAdapter(Context context, List<String> date, HashMap<String, List> hashMap) {
            this.context = context;
            this.hashMap = hashMap;
            this.date = date;

        }

        @Override
        public int getGroupCount() {
            return date.size()-1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return hashMap.get(date.get(groupPosition)).size();

        }

        @Override
        public Object getGroup(int groupPosition) {
            return date.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return hashMap.get(date.get(groupPosition)).get(childPosition);
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
                convertView = inflater.inflate(R.layout.date_list, null);
            }
            final TextView textView = (TextView) convertView.findViewById(R.id.datelist);
            textView.setTypeface(null, Typeface.BOLD);
            String str="";
            if(Integer.parseInt(month_cost.get(header_title))>ave){
                str="Date : "+header_title + ", Total cost : <font color=#C93F27>" + month_cost.get(header_title)+currency+"</font>, Extra expenses : <font color=#C93F27>"+(Integer.parseInt(month_cost.get(header_title))-ave)+currency+"</font>";

            }else {
                str="Date : "+header_title + ", Total cost : <font color=#fff>"  + month_cost.get(header_title)+currency+"</font>, Savings : <font color=#fff>"+(ave-Integer.parseInt(month_cost.get(header_title)))+currency+"</font>";

            }
            textView.setText(Html.fromHtml(str));
            head = header_title;



            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String children_string = (String) getChild(groupPosition, childPosition);


            String m = children_string.replaceAll("[{}]", "");
            String[] k = m.split("=");
            String cost_name, cost_amount;

            cost_name = k[0];
            cost_amount = k[1];


            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.date_cost_list, null);
            }
            final TextView textView = (TextView) convertView.findViewById(R.id.date_cost);
            final TextView cost = (TextView) convertView.findViewById(R.id.cost_amnt);
            textView.setText(cost_name);

                cost.setText(cost_amount+currency);

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(CostDetails.this).setTitle("Confirmation message")
                            .setMessage("Are you want to delete this information?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String id=childKey.get(date.get(groupPosition)).get(childPosition).toString();
                                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Registration").child(auth.getCurrentUser().getUid()).
                                            child("cost").child(year_str).child(month_str).child(date.get(groupPosition)).child(id);
                                    reference.removeValue();
                                    finish();
                                    startActivity(new Intent(CostDetails.this,ShowCostList.class));

                                }
                            }).setNegativeButton("No",null);
                    builder.show();

                    return true;
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
        startActivity(new Intent(CostDetails.this,ShowCostList.class));
        finish();    }


    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(CostDetails.this,MainActivity.class));

        }
    }
}
