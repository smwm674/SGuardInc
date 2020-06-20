package inc.sguard.android.com.sguardinc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import inc.sguard.android.com.sguardinc.Model.UserBooking;

public class UserCurrentOrder extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseReference reference;

    FirebaseUser user;
    RecyclerView recyclerView;
    ArrayList<UserBooking> list;
    UserCurrentOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_current_order);

        initToolBar();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("User").child(userid).child("Events").child("Current");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<UserBooking>();
                /*for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserBooking p = dataSnapshot1.getValue(UserBooking.class);
                    list.add(p);
                    Log.i("Current Order Booking", "name " + p.getCurrent_Booking());
                    Log.i("Current Order Booking", "number " + p.getCurrent_Booking_Number());
                }*/
                if (dataSnapshot.hasChildren()) {
                    UserBooking p = dataSnapshot.getValue(UserBooking.class);
                    Log.i("Current Order Booking", p.toString());
                    if (!p.equals(null)) {
                        list.add(p);
                        Log.i("Current Order Booking", "name " + p.getCurrent_Booking());
                        Log.i("Current Order Booking", "number " + p.getCurrent_Booking_Number());
                    }
                    if (list.size() > 0) {
                        adapter = new UserCurrentOrderAdapter(UserCurrentOrder.this, list);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserCurrentOrder.this, "unable to load the list of current orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.current_order);

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
        Intent intent = new Intent(UserCurrentOrder.this, UserHome.class);
        startActivity(intent);
        finish();
        //    super.onBackPressed();
    }
}
