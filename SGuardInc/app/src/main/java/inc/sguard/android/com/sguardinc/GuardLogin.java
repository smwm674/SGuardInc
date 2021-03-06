package inc.sguard.android.com.sguardinc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import inc.sguard.android.com.sguardinc.Model.Guard;

public class GuardLogin extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth auth;
    EditText Email, Password;
    TextView Register;
    Boolean match = false;
    DatabaseReference reference, child_refrencce;
    String lastchild;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //  initToolBar();

        auth = FirebaseAuth.getInstance();

        Email = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.pass);
        Register = (TextView) findViewById(R.id.register);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuardLogin.this, Registration.class);
                startActivity(intent);
                finish();
            }
        });

        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(GuardLogin.this, GuardHome.class);
                startActivity(intent);
                finish();*/

                final String email, password;
                email = Email.getText().toString();
                password = Password.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),
                            "All fields are required",
                            Toast.LENGTH_LONG).show();
                } else {
                    progressDialog = new ProgressDialog(GuardLogin.this);
                    progressDialog.setTitle("Authenticating...");
                    progressDialog.setMessage("Verifying email and password");
                    progressDialog.show();

                    reference = FirebaseDatabase.getInstance().getReference("Guard");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //   Log.v("total", "" +"total",dataSnapshot.getChildrenCount());
                            for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                Log.v("ID", "" + childDataSnapshot.getKey());
                                lastchild = childDataSnapshot.getKey();
                                if (match)
                                    break;

                                child_refrencce = FirebaseDatabase.getInstance().getReference("Guard")
                                        .child(childDataSnapshot.getKey());
                                child_refrencce.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.i("DataRecievied ", dataSnapshot.toString());
                                        Guard users = dataSnapshot.getValue(Guard.class);
                                        Log.i("DataRecievied Email", users.getEmail());

                                        if (users.getEmail().equals(email)) {
                                            Log.i("matched Email", users.getEmail() + "Matched");
                                            match = true;
                                            Log.i("match value", String.valueOf(match));
                                            VerifyUser(email, password);
                                        }

                                        if (lastchild.equals(childDataSnapshot.getKey())) {
                                            Log.i("match value last", String.valueOf(match));
                                            if (!match) {
                                                progressDialog.dismiss();
                                                Email.setText("");
                                                Password.setText("");
                                                Toast.makeText(getApplicationContext(), "no guard exists with this email", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        onBackPressed();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });

    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.guard_login);

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
        Intent intent = new Intent(GuardLogin.this, Login.class);
        startActivity(intent);
        finish();
        // super.onBackPressed();
    }

    void VerifyUser(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(GuardLogin.this, GuardHome.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else { /*Toast.makeText(getApplicationContext(),
                                    "Authentication Failed"
                                    , Toast.LENGTH_LONG).show();*/
                            progressDialog.dismiss();
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(GuardLogin.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            //  message.hide();
                            return;
                        }
                    }
                });

    }
}