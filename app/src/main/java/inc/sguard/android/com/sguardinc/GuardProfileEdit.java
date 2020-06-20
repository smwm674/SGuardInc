package inc.sguard.android.com.sguardinc;

import android.content.EntityIterator;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import de.hdodenhof.circleimageview.CircleImageView;
import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.Model.UserBooking;
import inc.sguard.android.com.sguardinc.Model.Vechile_Information;

public class GuardProfileEdit extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseUser user;
    DatabaseReference reference;
    EditText username, name, dob, vechile_number, model, car, pob, num, idnum, vehicle_type, vehicle_brand;
    CircleImageView image;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        initToolBar();

        username = (EditText) findViewById(R.id.username);
        name = (EditText) findViewById(R.id.name);
        dob = (EditText) findViewById(R.id.dob);
        vechile_number = (EditText) findViewById(R.id.vechile_number);
        pob = (EditText) findViewById(R.id.pob);
        num = (EditText) findViewById(R.id.num);
        idnum = (EditText) findViewById(R.id.idnum);
        vehicle_type = (EditText) findViewById(R.id.vtype);
        vehicle_brand = (EditText) findViewById(R.id.vbrand);
        car = (EditText) findViewById(R.id.car_name);
        model = (EditText) findViewById(R.id.model);

        image = (CircleImageView) findViewById(R.id.profile_pic);
        update = (Button) findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userid = user.getUid();
                reference = FirebaseDatabase.getInstance().getReference("Guard").child(userid);
                if (!(TextUtils.isEmpty(username.getText().toString())) && !(TextUtils.isEmpty(name.getText().toString())) && !(TextUtils.isEmpty(dob.getText().toString()))) {
                    reference.child("Username").setValue(username.getText().toString());
                    reference.child("Name").setValue(name.getText().toString());
                    reference.child("DateOfBirth").setValue(dob.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Some Fields are null", Toast.LENGTH_LONG).show();
                }

                reference = FirebaseDatabase.getInstance().getReference("Guard").child(userid).child("Additional_Information");
                HashMap<String, String> hashMap = new HashMap<>();
                if (!(TextUtils.isEmpty(vechile_number.getText().toString())) && !(TextUtils.isEmpty(idnum.getText().toString())) &&
                        !(TextUtils.isEmpty(num.getText().toString())) && !(TextUtils.isEmpty(pob.getText().toString())) &&
                        !(TextUtils.isEmpty(vehicle_type.getText().toString())) && !(TextUtils.isEmpty(vehicle_brand.getText().toString())) &&
                        !(TextUtils.isEmpty(model.getText().toString())) && !(TextUtils.isEmpty(car.getText().toString()))) {
                    hashMap.put("VehicleNumber", vechile_number.getText().toString());
                    hashMap.put("VehicleModel", model.getText().toString());
                    hashMap.put("VehicleColor", car.getText().toString());
                    hashMap.put("PlaceOfBirth", pob.getText().toString());
                    hashMap.put("MobileNumber", num.getText().toString());
                    hashMap.put("IdentityNumber", idnum.getText().toString());
                    hashMap.put("VehicleType", vehicle_type.getText().toString());
                    hashMap.put("VehicleBrand", vehicle_brand.getText().toString());
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(GuardProfileEdit.this, "Updated", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(GuardProfileEdit.this, "Make sure Vechile number mode and car name is not empty", Toast.LENGTH_LONG).show();
                }

            }
        });

        user = FirebaseAuth.getInstance().

                getCurrentUser();

        final String userid = user.getUid();
        reference = FirebaseDatabase.getInstance().

                getReference("Guard").

                child(userid);
        reference.addValueEventListener(new

                                                ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Log.i("DataRecievied ", dataSnapshot.toString());
                                                        Guard users = dataSnapshot.getValue(Guard.class);
                                                        if (users.getPhotoURL().toString().equals("default")) {
                                                        } else {
                                                            Picasso.get().load(users.getPhotoURL()).into(image);
                                                        }
                                                        username.setText(users.getUsername());
                                                        name.setText(users.getName());
                                                        dob.setText(users.getDateOfBirth());

                                                        if (dataSnapshot.hasChild("Additional_Information")) {
                                                            reference = FirebaseDatabase.getInstance().getReference("Guard").child(userid).child("Additional_Information");
                                                            reference.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    Vechile_Information vechile_information = dataSnapshot.getValue(Vechile_Information.class);
                                                                    vechile_number.setText(vechile_information.getVehicleNumber());
                                                                    model.setText(vechile_information.getVehicleModel());
                                                                    car.setText(vechile_information.getVehicleColor());
                                                                    num.setText(vechile_information.getMobileNumber());
                                                                    idnum.setText(vechile_information.getIdentityNumber());
                                                                    pob.setText(vechile_information.getPlaceOfBirth());
                                                                    vehicle_brand.setText(vechile_information.getVehicleBrand());
                                                                    vehicle_type.setText(vechile_information.getVehicleType());
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        onBackPressed();
                                                        Toast.makeText(GuardProfileEdit.this, "unable to load profile data of the user try again", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Guard Profile");

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
        Intent intent = new Intent(GuardProfileEdit.this, GuardHome.class);
        startActivity(intent);
        finish();
    }

}