package inc.sguard.android.com.sguardinc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class AdminGuardGallery extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    DatabaseReference reference;
    ArrayList<Guard> list;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_guard_gallery);

        initToolBar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setVisibility(View.INVISIBLE);

        reference = FirebaseDatabase.getInstance().getReference().child("Guard");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<Guard>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Guard p = dataSnapshot1.getValue(Guard.class);
                   if (!(dataSnapshot.child(p.getId()).hasChild("NotApproved")))
                    if (!(dataSnapshot.child(p.getId()).hasChild("SuspendGuard")))
                        list.add(p);
                }
                adapter = new MyAdapter(AdminGuardGallery.this, list);
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                toolbar.setTitle(R.string.guard_gallery);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminGuardGallery.this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        /*HashMap<String, Double> hashMap = new HashMap<>();
        hashMap.put("BaseFair", 0.25);
        hashMap.put("PerMile", 0.80);
        hashMap.put("PerHour", 5.0);
        reference = FirebaseDatabase.getInstance().getReference().child("Price");
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
        });*/


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                final Guard guard = list.get(position);
               /* list.remove(position);
                adapter.notifyDataSetChanged();*/
                reference = FirebaseDatabase.getInstance().getReference().child("Guard").child(guard.getId()).child("SuspendGuard");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Status", "True");
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if(list.size()>0)
                            list.remove(position);
                            adapter.notifyDataSetChanged();
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
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.guard_gallery);

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
        Intent intent = new Intent(AdminGuardGallery.this, AdminHome.class);
        startActivity(intent);
        finish();
    }
}
