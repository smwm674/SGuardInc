package inc.sguard.android.com.sguardinc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.UserChat.ChatDetails;
import inc.sguard.android.com.sguardinc.UserChat.chat;


public class UserBookOrder extends AppCompatActivity {

    Toolbar toolbar;
    TextView Next;
    EditText Purpose, ContactNumber;

    FirebaseUser user;
    DatabaseReference reference;

    RecyclerView recyclerView;
    ArrayList<Guard> list;
    MyAdapter adapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_order);

        initToolBar();

        user = FirebaseAuth.getInstance().getCurrentUser();

        Next = (TextView) findViewById(R.id.next);
        Purpose = (EditText) findViewById(R.id.purpose);
        ContactNumber = (EditText) findViewById(R.id.contact_number);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Guard guard = list.get(position);

                final String userid = user.getUid();
                reference = FirebaseDatabase.getInstance().getReference("User").child(userid).child("Events").child("Current");
                final SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Current_Booking", pref.getString("Current_Booking", null));
                hashMap.put("Current_Booking_Number", pref.getString("Current_Booking_Number", null));
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference = FirebaseDatabase.getInstance().getReference("User").child(userid).child("Pickup_Location");
                            Log.i("User id", userid);
                            //  Log.i("Lat Long", mMap.getCameraPosition().target.toString());

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("pickup_latitude", pref.getString("pickup_latitude", null));
                            hashMap.put("pickup_longitude", pref.getString("pickup_longitude", null));
                            hashMap.put("dropoff_latitude", pref.getString("dropoff_latitude", null));
                            hashMap.put("dropoff_longitude", pref.getString("dropoff_longitude", null));
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reference = FirebaseDatabase.getInstance().getReference("Guard").child(guard.getId()).child("Bookings").child("Current");
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        // hashMap.put("Current_Booking", pref.getString("Current_Booking", null));
                                        // hashMap.put("Current_Booking_Number", pref.getString("Current_Booking_Number", null));
                                        hashMap.put("Current_book_user", userid);
                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    /*Toast.makeText(getApplicationContext(), guard.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(UserBookOrder.this, UserHome.class);
                                                    startActivity(intent);
                                                    finish();*/
                                                    ChatDetails.chatWith = guard.getId();
                                                    ChatDetails.username = userid;
                                                    startActivity(new Intent(UserBookOrder.this, chat.class));

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Failed Problem Occured", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed Problem Occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Try Again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Purpose.getText().toString()) || !TextUtils.isEmpty(ContactNumber.getText().toString())) {

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Current_Booking", Purpose.getText().toString());
                    editor.putString("Current_Booking_Number", ContactNumber.getText().toString());
                    editor.commit();

                    reference = FirebaseDatabase.getInstance().getReference().child("Guard");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list = new ArrayList<Guard>();
                           /* for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Guard p = dataSnapshot1.getValue(Guard.class);
                                list.add(p);
                            }*/
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Guard p = dataSnapshot1.getValue(Guard.class);
                                if (!(dataSnapshot.child(p.getId()).hasChild("NotApproved")))
                                    if (!(dataSnapshot.child(p.getId()).hasChild("SuspendGuard")))
                                        list.add(p);
                            }

                            adapter = new MyAdapter(UserBookOrder.this, list);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            toolbar.setTitle(R.string.guard_gallery);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(UserBookOrder.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "All Fields are required", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.book_order);

        this.setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.INVISIBLE);
            toolbar.setTitle(R.string.enter_pickup_loc);
        } else {
            Intent intent = new Intent(UserBookOrder.this, UserHome.class);
            startActivity(intent);
            finish();
        }
        //    super.onBackPressed();
    }

}
