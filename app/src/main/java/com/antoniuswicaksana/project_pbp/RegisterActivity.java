package com.antoniuswicaksana.project_pbp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.antoniuswicaksana.project_pbp.api.UserApi;
import com.antoniuswicaksana.project_pbp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etNama, etAlamat, etTelp;
    private String email, password, nama, alamat, telp, userID, AESPassword = "password", encryptedPW;
    private Button btnCancel, btnSubmit;
    private Bitmap bitmap, resizedBitmap;
    private ProgressDialog progressDialog;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference, imageReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etTelp = findViewById(R.id.etTelp);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        databaseReference = firebaseDatabase.getReference("users");
        storageReference = firebaseStorage.getReference();

//        readDatabase();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail.getText().toString().isEmpty()) {
                    etEmail.setError("Isikan dengan benar");
                    etEmail.requestFocus();
                } else if(etPassword.getText().toString().isEmpty() && etPassword.length() < 6) {
                    etPassword.setError("Isikan dengan benar");
                    etPassword.requestFocus();
                } else if(etNama.getText().toString().isEmpty()) {
                    etNama.setError("Isikan dengan benar", null);
                    etNama.requestFocus();
                } else if(etAlamat.getText().toString().isEmpty()) {
                    etAlamat.setError("Isikan dengan benar", null);
                    etAlamat.requestFocus();
                } else if(etTelp.getText().toString().isEmpty()) {
                    etTelp.setError("Isikan dengan benar");
                    etTelp.requestFocus();
                } else {
                    email = etEmail.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    nama = etNama.getText().toString().trim();
                    alamat = etAlamat.getText().toString().trim();
                    telp = etTelp.getText().toString().trim();
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
                    bitmap = getResizedBitmap(bitmap, 512);
                    String imageString = encodeImage(bitmap);

                    user = new User(email, password, nama, alamat, telp);
                    registerUser(user, bitmap);
//                    saveUser(email, password, nama, alamat, telp, "");
                }
            }
        });

    }

    private void readDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("TAG", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String encodeImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void registerUser(User user, Bitmap bitmap) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading....");
        progressDialog.setTitle("Register in Progress");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    // send verification link
                    firebaseUser = firebaseAuth.getCurrentUser();
                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                        }
                    });

                    // SAVE USER TO DATABASE
                    encryptedPW = encrypt(AESPassword, password);
                    user.setPassword(encryptedPW);
                    userID = firebaseUser.getUid();
                    databaseReference.child(userID).setValue(user);

                    // SAVE IMAGE TO STORAGE
//                    imageReferences = storageReference.child(userID).child("Images").child("Profile Pic"); //{userID}/Images/Profile Pic.jpg
//                    UploadTask uploadTask = imageReferences.putFile(imagePath);

                    Toast.makeText(RegisterActivity.this, "User Has Been Saved", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    onBackPressed();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }
        });
    }

    private void saveUser(final String email, final String password, final String nama,
                          final String alamat, final String telp, final String image) {
        //Pendeklarasian queue
        RequestQueue queue = Volley.newRequestQueue(this);

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading....");
        progressDialog.setTitle("Menambahkan data user");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //Memulai membuat permintaan request menghapus data ke jaringan
        StringRequest stringRequest = new StringRequest(POST, UserApi.URL_ADD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Disini bagian jika response jaringan berhasil tidak terdapat ganguan/error
                progressDialog.dismiss();
                try {
                    //Mengubah response string menjadi object
                    JSONObject obj = new JSONObject(response);
                    //obj.getString("message") digunakan untuk mengambil pesan status dari response
                    if(obj.getString("message").equals("Add User Success"))
                    {
                        onBackPressed();
                    }

                    //obj.getString("message") digunakan untuk mengambil pesan message dari response
                    Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Disini bagian jika response jaringan terdapat ganguan/error
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                /*
                    Disini adalah proses memasukan/mengirimkan parameter key dengan data value,
                    dan nama key nya harus sesuai dengan parameter key yang diminta oleh jaringan
                    API.
                */
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("nama", nama);
                params.put("alamat", alamat);
                params.put("noTelp", telp);
                params.put("image", image);

                return params;
            }
        };

        //Disini proses penambahan request yang sudah kita buat ke reuest queue yang sudah dideklarasi
        queue.add(stringRequest);
    }

    private String encrypt(String AESPassword, String message) {
        String encryptedMsg = null;
        try {
            encryptedMsg = AESCrypt.encrypt(AESPassword, message);
        }catch (GeneralSecurityException e){
            e.printStackTrace();
        }
        return encryptedMsg;
    }

}