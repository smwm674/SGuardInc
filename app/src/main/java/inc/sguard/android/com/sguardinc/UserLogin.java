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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.Model.User;

public class UserLogin extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth auth;
    EditText Email, Password;
    TextView Register;
    DatabaseReference reference, child_refrencce;
    String lastchild;
    Boolean match = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // initToolBar();

        auth = FirebaseAuth.getInstance();

        Email = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.pass);
        Register = (TextView) findViewById(R.id.register);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLogin.this, Registration.class);
                startActivity(intent);
                finish();
            }
        });

        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(UserLogin.this, UserHome.class);
                startActivity(intent);
                finish();*/
                final String email, password;
                email = Email.getText().toString();
                password = Password.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),
                            "All fields are required",
                            Toast.LENGTH_LONG).show();
                } else{
                    progressDialog = new ProgressDialog(UserLogin.this);
                    progressDialog.setTitle("Authenticating...");
                    progressDialog.setMessage("Verifying email and password");
                    progressDialog.show();

                    reference = FirebaseDatabase.getInstance().getReference("User");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //   Log.v("total", "" +"total",dataSnapshot.getChildrenCount());
                            for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                Log.v("ID", "" + childDataSnapshot.getKey());
                                lastchild = childDataSnapshot.getKey();
                                if (match)
                                    break;

                                child_refrencce = FirebaseDatabase.getInstance().getReference("User")
                                        .child(childDataSnapshot.getKey());
                                child_refrencce.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.i("DataRecievied ", dataSnapshot.toString());
                                       User users = dataSnapshot.getValue(User.class);
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
                                                Toast.makeText(getApplicationContext(), "no user exists with this email", Toast.LENGTH_LONG).show();
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
                 //   VerifyUser(email, password);
            }
        });
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.user_login);

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
        Intent intent = new Intent(UserLogin.this, Login.class);
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
                            Intent intent = new Intent(UserLogin.this, UserHome.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            /*Toast.makeText(getApplicationContext(),
                                    "Authentication Failed"
                                    , Toast.LENGTH_LONG).show();*/
                         //   FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(UserLogin.this, "Failed Registration: " + task.getException(), Toast.LENGTH_SHORT).show();
                            //  message.hide();
                            return;
                        }
                    }
                });
    }
}
