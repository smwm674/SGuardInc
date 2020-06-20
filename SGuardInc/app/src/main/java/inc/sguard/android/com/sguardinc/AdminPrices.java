package inc.sguard.android.com.sguardinc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import inc.sguard.android.com.sguardinc.Model.Price;

public class AdminPrices extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseUser user;
    EditText basefair, permile, perhour;
    Button update;
    DatabaseReference reference;
    double Basefair, Permile, Perhour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_prices);
        initToolBar();

        basefair = (EditText) findViewById(R.id.basefair);
        permile = (EditText) findViewById(R.id.permile);
        perhour = (EditText) findViewById(R.id.perhour);
        update = (Button) findViewById(R.id.update);

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
                Toast.makeText(AdminPrices.this, "unable to load values try again later", Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference("Price");
                if (!(TextUtils.isEmpty(perhour.getText().toString())) && !(TextUtils.isEmpty(permile.getText().toString())) && !(TextUtils.isEmpty(basefair.getText().toString()))) {
                    reference.child("BaseFair").setValue(Double.valueOf(basefair.getText().toString()));
                    reference.child("PerHour").setValue(Double.valueOf(perhour.getText().toString()));
                    reference.child("PerMile").setValue(Double.valueOf(permile.getText().toString()));

                    Intent intent = new Intent(AdminPrices.this,AdminHome.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Some Fields are null", Toast.LENGTH_LONG).show();
                }
            }
        });

        //  user = FirebaseAuth.getInstance().getCurrentUser();

    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Change Price");

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

}
