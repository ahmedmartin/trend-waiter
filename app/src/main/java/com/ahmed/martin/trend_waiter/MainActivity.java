package com.ahmed.martin.trend_waiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView gv ;
    private ArrayAdapter <String> adapter ;
    private ArrayList <String> states=new ArrayList<>();
    private DatabaseReference table_ref;
    static String restaurant_name="Kfc";
    static String part_name;


    @Override
    protected void onStart() {
        super.onStart();
        get_data();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String email= FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        String s[] = email.split("_");
       // restaurant_name=s[0];
        part_name=s[1];

      gv =findViewById(R.id.grid_view);
      adapter=new ArrayAdapter<>(this,R.layout.table_list_show,R.id.status,states);
      gv.setAdapter(adapter);
      gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Intent info = new Intent(MainActivity.this,table_info.class);
              String[] s = states.get(position).split("\t");
              info.putExtra("table_number",s[0]);
              startActivity(info);
          }
      });


      table_ref = FirebaseDatabase.getInstance().getReference().child("resturants").child(restaurant_name)
              .child("part").child(part_name).child("table");


    }

    void get_data(){

      table_ref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              states.clear();
              for(DataSnapshot data : dataSnapshot.getChildren()) {
                  String s = data.getKey().toString();
                  s += "\t" + data.child("state").getValue().toString();
                  states.add(s);
                  adapter.notifyDataSetChanged();
              }
          }

          public void onCancelled(DatabaseError databaseError) {

          }
      });
    }


    public void booked_people(View view) {
      startActivity(new Intent(MainActivity.this,booked_list.class));
    }
}
