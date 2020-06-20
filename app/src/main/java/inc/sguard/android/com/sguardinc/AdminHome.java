package inc.sguard.android.com.sguardinc;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import inc.sguard.android.com.sguardinc.Model.Admin;
import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.Model.Price;

public class AdminHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference reference;
    // double Basefair, Permile, Perhour;
    // EditText basefair, permile, perhour;
    //Button update;
    ImageView profile;
    FirebaseUser user;

    String email, pass;
    String userid;
    RecyclerView recyclerView;
    ArrayList<Guard> list;
    AdminApprovalAdaptor adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Admin");
        toolbar.setTitleTextColor(getResources().getColor(R.color.blue));
        toolbar.setNavigationIcon(R.drawable.ic_dehaze);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setVisibility(View.INVISIBLE);

        user = FirebaseAuth.getInstance().getCurrentUser();
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
                Intent intent = new Intent(AdminHome.this, AdminProfileEdit.class);
                startActivity(intent);
                finish();

            }
        });

        /*reference = FirebaseDatabase.getInstance().getReference().child("Guard");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<Guard>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Guard p = dataSnapshot1.getValue(Guard.class);
                    if ((dataSnapshot.child(p.getId()).hasChild("NotApproved")))
                            list.add(p);
                }
                adapter = new AdminApprovalAdaptor(AdminHome.this, list);
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                toolbar.setTitle(R.string.guard_gallery);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminHome.this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        });*/

//        HashMap<String, Double> hashMap = new HashMap<>();
//        hashMap.put("BaseFair", 0.25);
//        hashMap.put("PerMile", 0.80);
//        hashMap.put("PerHour", 5.0);
//        reference = FirebaseDatabase.getInstance().getReference().child("Price");
//        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                }
//            }
//        });

        userid = user.getUid();

        /*HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("admin_id", userid);
        reference = FirebaseDatabase.getInstance().getReference().child("Admin_ID");
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
       });*/

        reference = FirebaseDatabase.getInstance().getReference("Admin").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("DataRecievied ", dataSnapshot.toString());
                Admin users = dataSnapshot.getValue(Admin.class);
                email = users.getEmail();
                pass = users.getPassword();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBackPressed();
                //  Toast.makeText(AdminProfileEdit.this, "unable to load profile data of the user try again", Toast.LENGTH_SHORT).show();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("Guard");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<Guard>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Guard p = dataSnapshot1.getValue(Guard.class);
                    if ((dataSnapshot.child(p.getId()).hasChild("NotApproved"))) {
                        list.add(p);
                        Log.i("approved", String.valueOf(p));
                    }
                }
                if (list.size() > 0) {
                    adapter = new AdminApprovalAdaptor(AdminHome.this, list, AdminHome.this, userid);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    toolbar.setTitle("Approve guard");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminHome.this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        });

      /*  basefair = (EditText) findViewById(R.id.basefair);
        permile = (EditText) findViewById(R.id.permile);
        perhour = (EditText) findViewById(R.id.perhour);
        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference("Price");
                if (!(TextUtils.isEmpty(perhour.getText().toString())) && !(TextUtils.isEmpty(permile.getText().toString())) && !(TextUtils.isEmpty(basefair.getText().toString()))) {
                    reference.child("BaseFair").setValue(Double.valueOf(basefair.getText().toString()));
                    reference.child("PerHour").setValue(Double.valueOf(perhour.getText().toString()));
                    reference.child("PerMile").setValue(Double.valueOf(permile.getText().toString()));
                } else {
                    Toast.makeText(getApplicationContext(), "Some Fields are null", Toast.LENGTH_LONG).show();
                }
            }
        });
        reference = FirebaseDatabase.getInstance().getReference().child("Price");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Price p = dataSnapshot.getValue(Price.class);
                Basefair = p.getBaseFair();
                Permile = p.getPerMile();
                Perhour = p.getPerHour();
                basefair.setText(String.valueOf(Basefair));
                permile.setText(String.valueOf(Permile));
                perhour.setText(String.valueOf(Perhour));
                Log.e("Price", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminHome.this, "unable to load values try again later", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_prices) {
            Intent intent = new Intent(AdminHome.this, AdminPrices.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_guard_gallery) {
            Intent intent = new Intent(AdminHome.this, AdminGuardGallery.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_guard) {
            Intent intent = new Intent(AdminHome.this, SuspendedGuard.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_pass) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change Password");

            /*final EditText email = new EditText(this);
            final EditText old_password = new EditText(this);
            final EditText new_password = new EditText(this);*/
            final EditText confirm_password = new EditText(this);
            //  email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
         /*   builder.setView(email);
            builder.setView(old_password);
            builder.setView(new_password);*/
            builder.setView(confirm_password);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, pass);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (confirm_password.getText().toString().length() >= 6) {
                                            user.updatePassword(confirm_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        reference = FirebaseDatabase.getInstance().getReference("Admin").child(userid);
                                                        reference.child("Password").setValue(confirm_password.getText().toString());
                                                        //  Log.d(TAG, "Password updated");
                                                    } else {
                                                        //  Log.d(TAG, "Error password not updated")
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Password Lenght must be atleast 6 characters"
                                                    , Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "try again later"
                                                , Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the UserHome/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminHome.this, Login.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

}
