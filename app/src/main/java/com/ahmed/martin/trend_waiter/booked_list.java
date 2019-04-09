package com.ahmed.martin.trend_waiter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class booked_list extends AppCompatActivity {

    ListView book_list;
    adapter adapt;
    ArrayList<String> time_list=new ArrayList<>();
    ArrayList<String> count_list=new ArrayList<>();
    ArrayList<String> users_list=new ArrayList<>();
    ArrayList<String> uid = new ArrayList<>();
    String time;
    DatabaseReference booked_ref;

    @Override
    protected void onStart() {
        super.onStart();

        booked_ref = FirebaseDatabase.getInstance().getReference().child("resturants")
                .child(MainActivity.restaurant_name).child("part").child(MainActivity.part_name).child("booked");
        booked_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                time_list.clear();
                users_list.clear();
                count_list.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    time_list.add(data.child("time").getValue().toString());
                    count_list.add(data.child("people_number").getValue().toString());
                    uid.add(data.getKey().toString());
                    get_user_data(data.getKey().toString());
                }
                booked_ref.removeEventListener(this);
            }

            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_list);

        book_list=findViewById(R.id.book_list);
        adapt=new adapter(this,users_list,time_list,count_list);
        book_list.setAdapter(adapt);
        registerForContextMenu(book_list);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.book_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(users_list.get(info.position));
            String[] menuItems = {"Done", "Cancel"};
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = {"Done", "Cancel"};
        String menuItemName = menuItems[menuItemIndex];

        if (menuItemName.equals("Done"))
            Done(uid.get(info.position));
        else
            Cancel(uid.get(info.position));

        return true;
    }

    private void Cancel(String user_id) {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("user").child(user_id);
        ref.child("table_booked").setValue("");
        ref.child("fine").setValue("10");
        int index = uid.indexOf(user_id);
        uid.remove(index);
        users_list.remove(index);
        time_list.remove(index);
        count_list.remove(index);
        adapt.notifyDataSetChanged();
        booked_ref.child(user_id).removeValue();
    }

    private void Done(String user_id) {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("user").child(user_id);
        ref.child("table_booked").setValue("");
        int index = uid.indexOf(user_id);
        uid.remove(index);
        users_list.remove(index);
        time_list.remove(index);
        count_list.remove(index);
        adapt.notifyDataSetChanged();
        booked_ref.child(user_id).removeValue();
    }

    private void get_user_data(String uid){
       final DatabaseReference users_ref =FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("personal info");
       users_ref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               user us = dataSnapshot.getValue(user.class);
               users_list.add(us.getFname()+" "+us.getLname());
               adapt.notifyDataSetChanged();
               users_ref.removeEventListener(this);
           }

           public void onCancelled(DatabaseError databaseError) {}
       });
    }





    public class adapter extends ArrayAdapter<String> {

        private final Activity context;
        private final ArrayList<String> user_name;
        private final ArrayList<String> time;
        private final ArrayList<String> count;

        public adapter(Activity context, ArrayList<String> user_name, ArrayList<String> time , ArrayList<String> count) {
            super(context, R.layout.table_check_show, user_name);
            // TODO Auto-generated constructor stub

            this.context=context;
            this.user_name=user_name;
            this.time=time;
            this.count=count;
        }

        public View getView(int position, View view, ViewGroup parent) {

            handler hand ;
            if(view==null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.table_check_show, null);
                hand = new handler(view);
                view.setTag(hand);
            }else
                hand = (handler) view.getTag();

            hand.getfood_name().setText(user_name.get(position));
            hand.getPrice().setText(time.get(position));
            hand.getCount().setText(count.get(position));


            return view;
        }


        public class handler {

            private View v;
            private TextView food_name;
            private TextView price;
            private TextView count;



            public handler(View v) {
                this.v = v;
            }

            public TextView getPrice() {
                if(price==null)
                    price=v.findViewById(R.id.prices);
                return price;
            }

            public TextView getCount() {
                if(count==null)
                    count=(TextView)v.findViewById(R.id.count);
                return count;
            }

            public void setPrice(TextView tv) {
                this.price = tv;
            }

            public void setCount(TextView iv) {
                this.count = iv;
            }


            public TextView getfood_name() {
                if(food_name==null)
                    food_name=(TextView)v.findViewById(R.id.food);
                return food_name;
            }

            public void setFood_name(TextView iv) {
                this.food_name = iv;
            }
        }
    }

}
