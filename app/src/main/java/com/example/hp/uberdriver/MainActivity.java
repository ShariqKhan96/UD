package com.example.hp.uberdriver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.hp.uberdriver.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    FButton SignIn, SignUp;
    MaterialEditText email;
    MaterialEditText password;
    MaterialEditText phone;
    MaterialEditText name;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    android.app.AlertDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/uber.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        SignIn = findViewById(R.id.signIn);
        SignUp = findViewById(R.id.singUp);

        dialog = new SpotsDialog(MainActivity.this);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignUpDialog();
            }
        });
    }

    private void showSignUpDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Register Account");
        alertDialog.setMessage("Please Fill All Information");


        View view = LayoutInflater.from(this).inflate(R.layout.signup_dialog, null);
        alertDialog.setView(view);

        phone = view.findViewById(R.id.phoneNumber);
        email = view.findViewById(R.id.Email);
        password = view.findViewById(R.id.Password);
        name = view.findViewById(R.id.Name);


        alertDialog.setPositiveButton("SIGN UP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialog.show();

                if (!TextUtils.isEmpty(phone.getText().toString()) &&
                        !TextUtils.isEmpty(email.getText().toString()) &&
                        !TextUtils.isEmpty(password.getText().toString()) &&
                        !TextUtils.isEmpty(name.getText().toString()))
                    if (password.getText().toString().length() < 6) {
                        phone.setError("Password should be greater than 6 digits!");
                    } else {
                        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        Log.e("authResult", authResult.getUser().getEmail());
                                        Toast.makeText(MainActivity.this, "" + authResult.getUser().getEmail(), Toast.LENGTH_SHORT).show();

                                        User user = new User(name.getText().toString(),
                                                password.getText().toString(),
                                                email.getText().toString(),
                                                phone.getText().toString());
                                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users");

                                        dbref.child(authResult.getUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "" + "User Registered!!", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(MainActivity.this, "USER NOT REGISTERED!!", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Exception", e.getMessage());
                            }
                        });
                    }


                dialogInterface.dismiss();
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showLoginDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Register Account");
        alertDialog.setMessage("Please Fill All Information");


        View view = LayoutInflater.from(this).inflate(R.layout.signin_dialog, null);
        alertDialog.setView(view);
        email = view.findViewById(R.id.Email);
        password = view.findViewById(R.id.Password);

        alertDialog.setPositiveButton("LOG IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SignIn.setEnabled(false);
                SignUp.setEnabled(false);
                dialog.show();

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "LOGIN Successfully!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Welcome.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SignIn.setEnabled(true);
                        SignUp.setEnabled(true);
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        alertDialog.show();
    }

}
