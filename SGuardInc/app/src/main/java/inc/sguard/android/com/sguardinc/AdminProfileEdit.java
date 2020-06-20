package inc.sguard.android.com.sguardinc;

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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import inc.sguard.android.com.sguardinc.Model.Admin;
import inc.sguard.android.com.sguardinc.Model.User;

public class AdminProfileEdit extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseUser user;
    DatabaseReference reference;
    EditText username, name, dob, password;
    CircleImageView image;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_edit);

        username = (EditText) findViewById(R.id.username);
        name = (EditText) findViewById(R.id.name);
        dob = (EditText) findViewById(R.id.dob);
        image = (CircleImageView) findViewById(R.id.profile_pic);
        update = (Button) findViewById(R.id.update);
        password = (EditText) findViewById(R.id.pass);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userid = user.getUid();
                reference = FirebaseDatabase.getInstance().getReference("Admin").child(userid);
                if (!(TextUtils.isEmpty(username.getText().toString())) && !(TextUtils.isEmpty(name.getText().toString())) && !(TextUtils.isEmpty(dob.getText().toString()))) {
                    reference.child("Username").setValue(username.getText().toString());
                    reference.child("Name").setValue(name.getText().toString());
                    reference.child("DateOfBirth").setValue(dob.getText().toString());
                    //  reference.child("Password").setValue(password.getText().toString());


                } else {
                    Toast.makeText(getApplicationContext(), "Some Fields are null", Toast.LENGTH_LONG).show();
                }
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Admin").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("DataRecievied ", dataSnapshot.toString());
                Admin users = dataSnapshot.getValue(Admin.class);
                if (users.getPhotoURL().toString().equals("default")) {
                } else {
                    Picasso.get().load(users.getPhotoURL()).into(image);
                }
                username.setText(users.getUsername());
                name.setText(users.getName());
                dob.setText(users.getDateOfBirth());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBackPressed();
                Toast.makeText(AdminProfileEdit.this, "unable to load profile data of the user try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
