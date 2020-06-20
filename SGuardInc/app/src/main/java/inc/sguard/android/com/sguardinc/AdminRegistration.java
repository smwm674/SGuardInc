package inc.sguard.android.com.sguardinc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class AdminRegistration extends AppCompatActivity {

    Toolbar toolbar;
    EditText FullName, UserName, Email, Password, ConfirmPassword;
    ImageView Profile;
    DatePicker DateOfBirth;
    Button Register;

    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;

    ProgressDialog progressDialog;

    String Date;
    private Uri filePath = null, imagepath = null;
    private final int PICK_IMAGE_REQUEST = 71;

    HashMap<String, String> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);

        initToolBar();

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
                Toast.makeText(AdminRegistration.this, Date, Toast.LENGTH_SHORT).show();
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
                        RegisterAdmin(fullname, username, email, password, Date);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Password doesnot match"
                            , Toast.LENGTH_LONG).show();
                }

            }
        });

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

    void uploadImage() {
        if (filePath != null) {
            progressDialog = new ProgressDialog(AdminRegistration.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // progressDialog.dismiss();
                            Log.i("Images Uploaded", String.valueOf(ref.getMetadata()));
                            Toast.makeText(AdminRegistration.this, "Uploaded", Toast.LENGTH_SHORT).show();

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
                                                Intent intent = new Intent(AdminRegistration.this, AdminHome.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
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
                            Toast.makeText(AdminRegistration.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    void RegisterAdmin(final String fullname, final String username, final String email, final String password, String DateOfBirth) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            String userid = user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Admin").child(userid);

                            hashMap.put("id", userid);
                            hashMap.put("Name", fullname);
                            hashMap.put("Username", username);
                            hashMap.put("Email", email);
                            hashMap.put("Password", password);
                            hashMap.put("DateOfBirth", Date);

                            /*if (status_layout.getVisibility() == View.VISIBLE)
                                hashMap.put("Status", status.getText().toString());*/

                            if (filePath != null) {
                                uploadImage();
                                progressDialog.dismiss();
                            } else {
                                progressDialog = new ProgressDialog(AdminRegistration.this);
                                progressDialog.setTitle("Uploading Credientials...");
                                progressDialog.show();

                                hashMap.put("PhotoURL", "default");

                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(AdminRegistration.this, AdminHome.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "You can't register with this email or password"
                                    , Toast.LENGTH_LONG).show();
                            //FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(AdminRegistration.this, "Failed Registration: " + task.getException(), Toast.LENGTH_SHORT).show();
                            //  message.hide();
                            return;
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdminRegistration.this, LoginOrSignup.class);
        startActivity(intent);
        finish();
    }
}
