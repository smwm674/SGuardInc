package inc.sguard.android.com.sguardinc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import inc.sguard.android.com.sguardinc.Glide.GlideApp;
import inc.sguard.android.com.sguardinc.Glide.MyGlideApp;
import inc.sguard.android.com.sguardinc.Model.Admin;
import inc.sguard.android.com.sguardinc.Model.AdminID;
import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.Model.User;
import inc.sguard.android.com.sguardinc.UserChat.AdminChat;
import inc.sguard.android.com.sguardinc.UserChat.ChatDetails;
import inc.sguard.android.com.sguardinc.UserChat.chat;

public class GuardHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView CurrentOrder, PreviousOrder;
    FirebaseUser user;
    DatabaseReference reference, verification_refrence;

    ImageView profile;

    RecyclerView recyclerView;
    ArrayList<User> list;
    ArrayList<String> guardlist;
    GuardCurrentOrderAdapter adapter;
    public static HashMap<String, Boolean> verification = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.current_order));
        toolbar.setTitleTextColor(getResources().getColor(R.color.blue));
        toolbar.setNavigationIcon(R.drawable.ic_dehaze);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);

        profile = (ImageView) headerview.findViewById(R.id.imageView);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuardHome.this, GuardProfileEdit.class);
                startActivity(intent);
                finish();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Guard").child(user.getUid());
        Log.i("Read from database", user.getUid());

        reference = FirebaseDatabase.getInstance().getReference("Guard").child(user.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("Verification")) {
                    // SharedPreferences.Editor editor = getSharedPreferences("verification", MODE_PRIVATE).edit();
                    verification.put("uploaded", true);
                    // editor.commit();
                } else {
                    verification.put("uploaded", false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Guard guard = dataSnapshot.getValue(Guard.class);
               /* if (guard.getPhotoURL().equals("default")) {
                    profile.setImageResource(R.drawable.prof);
                } else {
                    Log.i("Read from database", guard.getPhotoURL());
                    GlideApp.with(GuardHome.this).load(guard.getPhotoURL()).into(profile);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        CurrentOrder = (ImageView) findViewById(R.id.currentorder);
        CurrentOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuardHome.this, GuardCurrentOrder.class);
                startActivity(intent);
                finish();
            }
        });

        PreviousOrder = (ImageView) findViewById(R.id.previousorder);
        PreviousOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuardHome.this, GuardPreviousOrder.class);
                startActivity(intent);
                finish();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("Guard").child(user.getUid()).child("Bookings").child("Current");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                guardlist = new ArrayList<String>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.i("Read from database", reference.toString());
                    Log.i("Read from database", dataSnapshot1.toString());
                    Log.i("Read from database", dataSnapshot1.getValue().toString());
                    // GuardBooking p = dataSnapshot1.getValue(GuardBooking.class);
                    if (dataSnapshot1.exists())
                        guardlist.add((String) dataSnapshot1.getValue().toString());
                }
                //      Log.i("key", guardlist.get(0));
                if (guardlist.size() > 0) {
                    reference = FirebaseDatabase.getInstance().getReference().child("User").child(guardlist.get(0).toString());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list = new ArrayList<User>();
                            /*for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Log.i("Read from database", reference.toString());
                                Log.i("Read from database", dataSnapshot1.toString());
                                Log.i("Read from database", dataSnapshot1.getValue().toString());

                                User p = dataSnapshot1.getValue(User.class);
                                list.add(p);
                            }*/
                            if (dataSnapshot.exists()) {
                                User p = dataSnapshot.getValue(User.class);
                                Log.i("LIst size object ", String.valueOf(p));
                                list.add(p);
                                Log.i("Read from database", String.valueOf(p));
                                Log.i("LIst size", String.valueOf(list.size()));
                                Log.i("LIst size data", String.valueOf(list));
                                if (list.size() > 0) {
                                    adapter = new GuardCurrentOrderAdapter(GuardHome.this, list);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                /* list = new ArrayList<User>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User p = dataSnapshot1.getValue(User.class);
                    list.add(p);
                }
                if (list.size() > 0) {
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GuardHome.this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                ChatDetails.chatWith = list.get(position).getId();
                ChatDetails.username = user.getUid();
                startActivity(new Intent(GuardHome.this, chat.class));

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guard_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(GuardHome.this, Login.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/
        if (id == R.id.nav_guard_gallery) {
            // Handle the camera action
        } else if (id == R.id.nav_our_mission) {
            Intent intent = new Intent(GuardHome.this, OurMissionGuard.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_job_requiement) {
            Intent intent = new Intent(GuardHome.this, JobRequirementGuard.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_job_description) {
            Intent intent = new Intent(GuardHome.this, JobDescriptionGuard.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_how_it_works) {
            Intent intent = new Intent(GuardHome.this, HowItWorksGuard.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_verification) {
            Intent intent = new Intent(GuardHome.this, Guard_Verification.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_admin_message) {

            reference = FirebaseDatabase.getInstance().getReference().child("Admin_ID");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i("DataRecievied ", dataSnapshot.toString());
                    AdminID users = dataSnapshot.getValue(AdminID.class);
                    Log.i("admin_id",users.getAdmin_id());
                    String id = users.getAdmin_id();
                    ChatDetails.chatWith = id;
                    ChatDetails.username = user.getUid();
                    startActivity(new Intent(GuardHome.this, AdminChat.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onBackPressed();
                    //  Toast.makeText(AdminProfileEdit.this, "unable to load profile data of the user try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
