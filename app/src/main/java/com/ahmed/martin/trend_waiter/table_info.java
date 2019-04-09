package com.ahmed.martin.trend_waiter;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class table_info extends AppCompatActivity {

    private DatabaseReference table_info_ref;
    private DatabaseReference read_ref;
    private DatabaseReference unread_ref;
    private DatabaseReference user_ref;
    private ChildEventListener read_listen;
    private ChildEventListener unread_listen;

    private TextView total;
    private TextView fname;
    private TextView lname;
    private TextView phone;


    private String table_number;
    private String uid;
    private String state;
    private double cash_sum;

    private ArrayList<String> unread_food=new ArrayList<>();
    private ArrayList <String>read_food=new ArrayList<>();
    private ArrayList <Double>unread_price =new ArrayList<>();
    private ArrayList <Double>read_price=new ArrayList<>();
    private ArrayList <Integer>unread_count=new ArrayList<>();
    private ArrayList <Integer>read_count=new ArrayList<>();

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private ListView unraed_food_list;
    private ListView read_food_list;
    private table_adapter unread_adapt;
    private table_adapter read_adapt;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_info);

        table_number=getIntent().getStringExtra("table_number");

        total=findViewById(R.id.total);
        final Button b =findViewById(R.id.btn_state);
        fname=findViewById(R.id.fname);
        lname=findViewById(R.id.lname);
        phone=findViewById(R.id.phone);




        table_info_ref = FirebaseDatabase.getInstance().getReference().child("resturants").child(MainActivity.restaurant_name)
                .child("part").child(MainActivity.part_name).child("table").child(table_number);
        table_info_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    uid = dataSnapshot.child("uid").getValue().toString();
                    get_user_data();
                    state = dataSnapshot.child("state").getValue().toString();
                    if (state.equals("غلق الطاوله"))
                        b.setText("Close");
                    if(state.equals("جارى الطلب"))
                        b.setVisibility(View.INVISIBLE);
                    if(state.equals("طعام مضاف")||state.equals("غلق الطاوله"))
                        b.setVisibility(View.VISIBLE);
                }

            }
            public void onCancelled(DatabaseError databaseError) {}
        });


        unraed_food_list =findViewById(R.id.unread_order);
        unread_adapt = new table_adapter(table_info.this, unread_food, unread_price,unread_count);
        unraed_food_list.setAdapter(unread_adapt);


        read_food_list =findViewById(R.id.read_order);
        read_adapt = new table_adapter(table_info.this, read_food, read_price,read_count);
        read_food_list.setAdapter(read_adapt);

        get_read_data();
        get_unread_data();
    }

    private void get_user_data() {
       user_ref=FirebaseDatabase.getInstance().getReference().child("user").child(uid);
       user_ref.child("personal info").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               user use = dataSnapshot.getValue(user.class);
               fname.setText(use.getFname());
               lname.setText(use.getLname());
               phone.setText(use.getPhone());
           }
           public void onCancelled(DatabaseError databaseError) {}
       });


    }


    private void get_unread_data(){
        unread_listen = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String ss) {
                if(dataSnapshot.exists()) {
                    unread_food.add(dataSnapshot.getKey().toString());
                    String s = dataSnapshot.getValue().toString();
                    String a[] = s.split("x");
                    unread_price.add(Double.parseDouble(a[0]));
                    unread_count.add(Integer.parseInt(a[1]));
                    unread_adapt.notifyDataSetChanged();
                }
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String ss) {        }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int position = unread_food.indexOf(dataSnapshot.getKey().toString());
                unread_food.remove(position);
                unread_price.remove(position);
                unread_count.remove(position);
                unread_adapt.notifyDataSetChanged();

            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };

        unread_ref.addChildEventListener(unread_listen);
       // unread_ref.removeEventListener(unread_listen);
    }


    public void get_read_data(){
        cash_sum=0;

        unread_ref=table_info_ref.child("unread");
        read_ref=table_info_ref.child("read");




        read_listen = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String ss) {
                read_food.add(dataSnapshot.getKey().toString());
                String s = dataSnapshot.getValue().toString();
                String a[] = s.split("x");
                read_price.add(Double.parseDouble(a[0]));
                read_count.add(Integer.parseInt(a[1]));
                cash_sum += (Double.parseDouble(a[0]) * Integer.parseInt(a[1]));
                total.setText("TOTAL : " + Double.valueOf(decimalFormat.format(cash_sum)));
                read_adapt.notifyDataSetChanged();
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String ss) {
                int position;
                position = read_food.indexOf(dataSnapshot.getKey().toString());
                cash_sum-=(read_count.get(position)*read_price.get(position));
                String s = dataSnapshot.getValue().toString();
                String a[] = s.split("x");
                read_count.set(position,Integer.parseInt(a[1]));
                cash_sum+=(read_count.get(position)*read_price.get(position));
                total.setText("TOTAL : " +Double.valueOf(decimalFormat.format(cash_sum)));
                read_adapt.notifyDataSetChanged();
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int position;
                position = read_food.indexOf(dataSnapshot.getKey().toString());
                read_food.remove(dataSnapshot.getKey().toString());
                cash_sum-=(read_price.get(position)*read_count.get(position));
                total.setText("TOTAL : " + Double.valueOf(decimalFormat.format(cash_sum)));
                read_count.remove(position);
                read_price.remove(position);
                read_adapt.notifyDataSetChanged();
                if(read_food.size()==0)
                    total.setText("TOTAL : " + 0);
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };

        read_ref.addChildEventListener(read_listen);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        read_ref.removeEventListener(read_listen);
        unread_ref.removeEventListener(unread_listen);
    }

    private String reason;
    public void cancel(View view) {
        LinearLayout layout = new LinearLayout(table_info.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Spinner spinner =new Spinner(table_info.this);
        final String data[]={"fake order","user canceled order"};
        ArrayAdapter<String> adapt =new ArrayAdapter<>(table_info.this,android.R.layout.simple_spinner_item,data);
        adapt.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapt);
        LinearLayout.LayoutParams param  =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins(40,60,40,40);
        spinner.setLayoutParams(param);
        spinner.setGravity(Gravity.CENTER);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reason = data[position];
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final EditText text = new EditText(table_info.this);
        text.setHint("Describe Reason what happened ??!!......");
        text.setPadding(20,0,20,0);
        LinearLayout.LayoutParams params  =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,300 );
        params.setMargins(20,0,20,20);
        text.setLayoutParams(params);
        text.setTextSize(18);
        text.setTextColor(Color.BLACK);

        layout.addView(spinner);
        layout.addView(text);

        AlertDialog.Builder alart = new AlertDialog.Builder(table_info.this);
        alart.setMessage("Choose reason and Write it in Description ..!!");
        alart.setView(layout);
        alart.setPositiveButton("send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(reason)){
                    Toast.makeText(table_info.this, "choose reason", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(text.getText().toString())){
                    Toast.makeText(table_info.this, "Write reason", Toast.LENGTH_SHORT).show();
                }else{
                    Date d =new Date();
                    SimpleDateFormat day = new SimpleDateFormat("dd-MM-yy");
                    SimpleDateFormat hour = new SimpleDateFormat("hh:mm:ss");
                    DatabaseReference cancel_order = FirebaseDatabase.getInstance().getReference().child("trend").child("canceled")
                            .child(day.format(d).toString()).child(hour.format(d).toString());
                    cancel_order.child("uid").setValue(uid);
                    cancel_order.child("reason").setValue(reason);
                    cancel_order.child("description").setValue(text.getText().toString());
                    table_info_ref.removeValue();
                    user_ref.child("table details").setValue(" ");
                    finish();
                }
            }
        });
        alart.show();


    }

    public void btn_state(View view) {

        final Date d =new Date();
        SimpleDateFormat day = new SimpleDateFormat("dd-MM-yy");
        SimpleDateFormat hour = new SimpleDateFormat("hh:mm:ss");

        DatabaseReference finish_ref =FirebaseDatabase.getInstance().getReference().child("trend").child("finished")
                .child(MainActivity.restaurant_name).child("table").child(day.format(d).toString()).child(hour.format(d).toString());

        if(state.equals("غلق الطاوله")){
            for(int i=0;i<read_food.size();i++){
                finish_ref.child(read_food.get(i)).setValue(read_price.get(i)+"x"+read_count.get(i));
            }
            table_info_ref.removeValue();
            user_ref.child("table details").setValue(" ");
            finish();
        }else{
            for(int i=0;i<unread_food.size();i++){
                if(read_food.contains(unread_food.get(i))){
                    int position = read_food.indexOf(unread_food.get(i));
                    int new_count=unread_count.get(i)+read_count.get(position);
                    read_ref.child(unread_food.get(i)).setValue(unread_price.get(i)+"x"+new_count);
                }else{
                    read_ref.child(unread_food.get(i)).setValue(unread_price.get(i)+"x"+unread_count.get(i));
                }
            }
            unread_ref.removeValue();
            table_info_ref.child("state").setValue("جارى الطلب");
        }



    }
}
