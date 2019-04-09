package com.ahmed.martin.trend_waiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class sign_in extends AppCompatActivity {

     EditText email;
     EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email=findViewById(R.id.sign_in_email);
        password=findViewById(R.id.sign_in_password);
    }


        @Override
        public void onStart() {
            super.onStart();

            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if(currentUser!=null){//&&currentUser.isEmailVerified()) {
                finish();
                startActivity(new Intent(sign_in.this,MainActivity.class));
            }
        }

    public void sign_in(View view) {
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)){
            Toast.makeText(this,"يجب ادخال جميع البيانات",Toast.LENGTH_LONG).show();
        }else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                finish();
                                startActivity(new Intent(sign_in.this,MainActivity.class));

                            }else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

}
