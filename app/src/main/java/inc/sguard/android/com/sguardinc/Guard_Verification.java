package inc.sguard.android.com.sguardinc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Guard_Verification extends AppCompatActivity {
    Toolbar toolbar;
    Button upload;
    LinearLayout verified;
    ImageView id_front, id_back, driving_front, driving_back;
    ProgressDialog progressDialog;
    Uri id_front_path = null, id_back_path = null, driving_front_path = null, driving_back_path = null;

    FirebaseUser user;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    HashMap<String, String> hashMap = new HashMap<>();
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard__verification);
        initToolBar();

        GuardHome CheckVerification = new GuardHome();
        user = FirebaseAuth.getInstance().getCurrentUser();
        /*SharedPreferences.Editor editor = getSharedPreferences("verification", MODE_PRIVATE).edit();
        editor.putBoolean("uploaded", true);
        editor.commit();*/

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        reference = FirebaseDatabase.getInstance().getReference("Guard").child(user.getUid()).child("Verification");

        verified = (LinearLayout) findViewById(R.id.verified);
        if (CheckVerification.verification.get("uploaded")) {
            verified.setVisibility(View.VISIBLE);
        }

        id_front = (ImageView) findViewById(R.id.id_front);
        id_back = (ImageView) findViewById(R.id.id_back);
        driving_front = (ImageView) findViewById(R.id.driving_front);
        driving_back = (ImageView) findViewById(R.id.driving_back);


        upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((id_front_path != null) && (id_back_path != null) && (driving_front_path != null) && (driving_back_path != null)) {
                    uploadImage();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select all images from the gallery", Toast.LENGTH_LONG).show();
                }
            }
        });

        id_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        id_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
            }
        });

        driving_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
            }
        });

        driving_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
            }
        });
    }

    void uploadImage() {
        progressDialog = new ProgressDialog(Guard_Verification.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        ref = storageReference.child("verification/" + UUID.randomUUID().toString());
        ref.putFile(id_front_path)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // progressDialog.dismiss();
                        Log.i("Images Uploaded", String.valueOf(ref.getMetadata()));
                        //  Toast.makeText(Guard_Verification.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("Images Uploaded", "onSuccess: uri= " + uri.toString());
                                if (uri != null) {
                                    Log.i("PhotoURL", uri.toString());
                                    hashMap.put("id_front", uri.toString());

                                    ref = storageReference.child("verification/" + UUID.randomUUID().toString());
                                    ref.putFile(id_back_path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri urii) {
                                                    if (urii != null) {
                                                        hashMap.put("id_back", urii.toString());
                                                        ref = storageReference.child("verification/" + UUID.randomUUID().toString());
                                                        ref.putFile(driving_front_path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri driving_front) {
                                                                        if (driving_front != null) {
                                                                            hashMap.put("driving_front", driving_front.toString());
                                                                            ref = storageReference.child("verification/" + UUID.randomUUID().toString());
                                                                            ref.putFile(driving_back_path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                        @Override
                                                                                        public void onSuccess(Uri driving_back) {
                                                                                            if (driving_back != null)
                                                                                                hashMap.put("driving_back", driving_back.toString());

                                                                                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        verified.setVisibility(View.VISIBLE);
                                                                                                        progressDialog.dismiss();
                                                                                                        Toast.makeText(getApplicationContext(), "Verified", Toast.LENGTH_LONG).show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    // progressDialog.dismiss();
                                                                                    Log.i("PhotoURL", "default");
                                                                                    hashMap.put("driving_back", "default");
                                                                                    Toast.makeText(Guard_Verification.this, "Failed try again later " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                                                            .getTotalByteCount());
                                                                                    progressDialog.setTitle("Driving License Back");
                                                                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                                                                }
                                                                            });
                                                                        }

                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // progressDialog.dismiss();
                                                                Log.i("PhotoURL", "default");
                                                                hashMap.put("driving_front", "default");
                                                                Toast.makeText(Guard_Verification.this, "Failed try again later " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                                        .getTotalByteCount());

                                                                progressDialog.setTitle("ID Card Front");
                                                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // progressDialog.dismiss();
                                            Log.i("PhotoURL", "default");
                                            hashMap.put("id_back", "default");
                                            Toast.makeText(Guard_Verification.this, "Failed try again later " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                    .getTotalByteCount());
                                            progressDialog.setTitle("ID Card Back");
                                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                        }
                                    });
                                }


                                /*reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (current.getText().toString().equals("User")) {
                                                Intent intent = new Intent(Registration.this, UserHome.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(Registration.this, GuardHome.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                });*/
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // progressDialog.dismiss();
                        Log.i("PhotoURL", "default");
                        hashMap.put("id_front", "default");
                        Toast.makeText(Guard_Verification.this, "Failed try again later " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setTitle("ID Card Front");
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            id_front_path = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), id_front_path);
                id_front.setImageBitmap(bitmap);
                id_front.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            id_back_path = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), id_back_path);
                id_back.setImageBitmap(bitmap);
                id_back.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            driving_front_path = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), driving_front_path);
                driving_front.setImageBitmap(bitmap);
                driving_front.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 4 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            driving_back_path = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), driving_back_path);
                driving_back.setImageBitmap(bitmap);
                driving_back.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Guard Verification");

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
        Intent intent = new Intent(Guard_Verification.this, GuardHome.class);
        startActivity(intent);
        finish();
    }
}
