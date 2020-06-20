package inc.sguard.android.com.sguardinc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference reference;

    @Override
    protected void onStart() {
        super.onStart();
        /* user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i("Splash", "User Login in ");
            final String userid = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference("User");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Log.i("Splash", "Destected User Login in ");
                        Log.v("ID", "" + childDataSnapshot.getKey());
                        if (childDataSnapshot.getKey().toString().equals(userid)) {
                            Intent intent = new Intent(SplashScreen.this, UserHome.class);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }
                    reference = FirebaseDatabase.getInstance().getReference("Guard");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                Log.i("Splash", "Destected Guard Login in ");
                                Log.v("ID", "" + childDataSnapshot.getKey());
                                if (childDataSnapshot.getKey().toString().equals(userid)) {
                                    Intent intent = new Intent(SplashScreen.this, GuardHome.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                {
                    if (isNetworkConnected()) {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            Log.i("Splash", "User Login in ");
                            final String userid = user.getUid();
                            Log.i("Splash", "User id " + userid);
                            reference = FirebaseDatabase.getInstance().getReference("User");
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                        Log.v("ID", "" + childDataSnapshot.getKey());
                                        if (childDataSnapshot.getKey().toString().equals(userid)) {
                                            Log.i("Splash", "Destected User Login in ");
                                            Intent intent = new Intent(SplashScreen.this, UserHome.class);
                                            startActivity(intent);
                                            finish();
                                            break;
                                        }
                                    }
                                    reference = FirebaseDatabase.getInstance().getReference("Guard");
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                Log.v("ID", "" + childDataSnapshot.getKey());
                                                if (childDataSnapshot.getKey().toString().equals(userid)) {
                                                    Log.i("Splash", "Destected Guard Login in ");
                                                    Intent intent = new Intent(SplashScreen.this, GuardHome.class);
                                                    startActivity(intent);
                                                    finish();
                                                    break;
                                                }
                                            }

                                            reference = FirebaseDatabase.getInstance().getReference("Admin");
                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                        Log.v("ID", "" + childDataSnapshot.getKey());
                                                        if (childDataSnapshot.getKey().toString().equals(userid)) {
                                                            Log.i("Splash", "Destected Guard Login in ");
                                                            Intent intent = new Intent(SplashScreen.this, AdminHome.class);
                                                            startActivity(intent);
                                                            finish();
                                                            break;
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Intent i = new Intent(SplashScreen.this, LoginOrSignup.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Intent i = new Intent(SplashScreen.this, LoginOrSignup.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        }, 2000);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
