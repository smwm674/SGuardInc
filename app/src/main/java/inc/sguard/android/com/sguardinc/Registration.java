package inc.sguard.android.com.sguardinc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ycuwq.datepicker.date.DatePicker;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import it.beppi.tristatetogglebutton_library.TriStateToggleButton;

public class Registration extends AppCompatActivity {

    Toolbar toolbar;
    TriStateToggleButton user_guard_toggle, status_guard_toggle;
    TextView current, status;
    EditText FullName, UserName, Email, Password, ConfirmPassword;
    ImageView Profile;
    DatePicker DateOfBirth;
    Button Register;
    LinearLayout status_layout;

    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;

    ProgressDialog progressDialog;

    HashMap<String, String> hashMap = new HashMap<>();

    String Date;
    private Uri filePath = null, imagepath = null;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initToolBar();

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        status_layout = (LinearLayout) findViewById(R.id.status_layout);
        current = (TextView) findViewById(R.id.current);
        status = (TextView) findViewById(R.id.status);
        user_guard_toggle = (TriStateToggleButton) findViewById(R.id.user_guard_toggle);
        status_guard_toggle = (TriStateToggleButton) findViewById(R.id.status_guard_toggle);

        user_guard_toggle.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                if (booleanToggleStatus == false) {
                    current.setText(R.string.user);
                    if (!(status_layout.getVisibility() == View.GONE)) {
                        status_layout.setVisibility(View.GONE);
                    }
                } else {
                    current.setText(R.string.guard);
                    if (status_layout.getVisibility() == View.GONE) {
                        status_layout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        status_guard_toggle.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                if (booleanToggleStatus == false) {
                    status.setText(R.string.armed);
                } else {
                    status.setText(R.string.unarmed);
                }
            }
        });

        FullName = (EditText) findViewById(R.id.fullname);
        UserName = (EditText) findViewById(R.id.username);
        Email = (EditText) findViewById(R.id.email);
        Profile = (ImageView) findViewById(R.id.pic);
        Password = (EditText) findViewById(R.id.pass);
        ConfirmPassword = (EditText) findViewById(R.id.confirmPass);
        DateOfBirth = (DatePicker) findViewById(R.id.datePicker);
        Register = (Button) findViewById(R.id.register_button);

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        DateOfBirth.setOnDateSelectedListener(new DatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                Date = (year + "-" + month + "-" + day);
                Toast.makeText(Registration.this, Date, Toast.LENGTH_SHORT).show();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username, password, email, confirmpass, fullname, dob;
                username = UserName.getText().toString();
                email = Email.getText().toString();
                password = Password.getText().toString();
                confirmpass = ConfirmPassword.getText().toString();
                fullname = FullName.getText().toString();

                if (TextUtils.equals(password, confirmpass)) {
                    if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(Date)) {
                        Toast.makeText(getApplicationContext(),
                                "All fields are required"
                                , Toast.LENGTH_LONG).show();
                    } else if (password.length() < 6) {
                        Toast.makeText(getApplicationContext(),
                                "Password Lenght must be atleast 6 characters"
                                , Toast.LENGTH_LONG).show();
                    } else
                        RegisterUser(fullname, username, email, password, Date);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Password doesnot match"
                            , Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void uploadImage(final String userid) {
        if (filePath != null) {
            progressDialog = new ProgressDialog(Registration.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // progressDialog.dismiss();
                            Log.i("Images Uploaded", String.valueOf(ref.getMetadata()));
                            Toast.makeText(Registration.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("Images Uploaded", "onSuccess: uri= " + uri.toString());
                                    imagepath = uri;

                                    if (imagepath != null) {
                                        Log.i("PhotoURL", imagepath.toString());
                                        hashMap.put("PhotoURL", imagepath.toString());
                                    } else {
                                        Log.i("PhotoURL", "default");
                                        hashMap.put("PhotoURL", "default");
                                    }


                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (current.getText().toString().equals("User")) {
                                                    Intent intent = new Intent(Registration.this, UserHome.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.i("NotApproved","scope");
                                                    reference = FirebaseDatabase.getInstance().getReference(current.getText().toString()).child(userid).child("NotApproved");
                                                    HashMap<String, String> hashMap = new HashMap<>();
                                                    hashMap.put("Status", "True");
                                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.i("NotApproved","added");
                                                                progressDialog.dismiss();
                                                                Intent intent = new Intent(Registration.this, GuardHome.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    /* Intent intent = new Intent(Registration.this, GuardHome.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();*/
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // progressDialog.dismiss();
                            Log.i("PhotoURL", "default");
                            hashMap.put("PhotoURL", "default");
                            Toast.makeText(Registration.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.register);

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
        Intent intent = new Intent(Registration.this, LoginOrSignup.class);
        startActivity(intent);
        finish();
        // super.onBackPressed();
    }

    void RegisterUser(final String fullname, final String username, final String email, final String password, String DateOfBirth) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            final String userid = user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference(current.getText().toString()).child(userid);

                            hashMap.put("id", userid);
                            hashMap.put("Name", fullname);
                            hashMap.put("Username", username);
                            hashMap.put("Email", email);
                            hashMap.put("Password", password);
                            hashMap.put("DateOfBirth", Date);

                            if (status_layout.getVisibility() == View.VISIBLE)
                                hashMap.put("Status", status.getText().toString());

                            if (filePath != null) {
                                uploadImage(userid);
                                progressDialog.dismiss();
                            } else {
                                progressDialog = new ProgressDialog(Registration.this);
                                progressDialog.setTitle("Uploading Credientials...");
                                progressDialog.show();

                                hashMap.put("PhotoURL", "default");

                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (current.getText().toString().equals("User")) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(Registration.this, UserHome.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Log.i("NotApproved","scope");
                                                reference = FirebaseDatabase.getInstance().getReference(current.getText().toString()).child(userid).child("NotApproved");
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("Status", "True");
                                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.i("NotApproved","added");
                                                            progressDialog.dismiss();
                                                            Intent intent = new Intent(Registration.this, GuardHome.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    }
                                });
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "You can't register with this email or password"
                                    , Toast.LENGTH_LONG).show();
                            //FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(Registration.this, "Failed Registration: " + task.getException(), Toast.LENGTH_SHORT).show();
                            //  message.hide();
                            return;
                        }
                    }
                });
    }
}
