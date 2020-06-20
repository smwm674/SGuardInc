package inc.sguard.android.com.sguardinc;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.Model.Vechile_Information;
import inc.sguard.android.com.sguardinc.Model.Verification;

public class AdminViewProfileGuard extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseReference reference;
    EditText username, name, dob, vechile_number, model, car, pob, num, idnum, vehicle_type, vehicle_brand;
    CircleImageView image;
    TextView data;
    String guard_id;
    RelativeLayout layout;
    ImageView id_front, id_back, driving_front, driving_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_profile_guard);

        guard_id = getIntent().getStringExtra("VIEW_GUARD_ID");
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
        data = (TextView) findViewById(R.id.data);
        id_front = (ImageView) findViewById(R.id.id_front);
        id_back = (ImageView) findViewById(R.id.id_back);
        driving_front = (ImageView) findViewById(R.id.driving_front);
        driving_back = (ImageView) findViewById(R.id.driving_back);
        layout = (RelativeLayout) findViewById(R.id.ver);

        reference = FirebaseDatabase.getInstance().getReference("Guard").child(guard_id);
        reference.addValueEventListener(new ValueEventListener() {
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

                if (dataSnapshot.hasChild("Verification")) {
                    layout.setVisibility(View.VISIBLE);
                    reference = FirebaseDatabase.getInstance().getReference("Guard").child(guard_id).child("Verification");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Verification verification = dataSnapshot.getValue(Verification.class);
                            Picasso.get().load(verification.getId_front()).into(id_front);
                            Picasso.get().load(verification.getId_back()).into(id_back);
                            Picasso.get().load(verification.getDriving_back()).into(driving_back);
                            Picasso.get().load(verification.getDriving_front()).into(driving_front);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                if (dataSnapshot.hasChild("Additional_Information")) {
                    vehicle_brand.setVisibility(View.VISIBLE);
                    pob.setVisibility(View.VISIBLE);
                    idnum.setVisibility(View.VISIBLE);
                    num.setVisibility(View.VISIBLE);
                    data.setVisibility(View.VISIBLE);
                    vechile_number.setVisibility(View.VISIBLE);
                    car.setVisibility(View.VISIBLE);
                    vehicle_type.setVisibility(View.VISIBLE);
                    model.setVisibility(View.VISIBLE);

                    reference = FirebaseDatabase.getInstance().getReference("Guard").child(guard_id).child("Additional_Information");
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
                Toast.makeText(AdminViewProfileGuard.this, "unable to load profile data of the user try again", Toast.LENGTH_SHORT).show();
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
}
