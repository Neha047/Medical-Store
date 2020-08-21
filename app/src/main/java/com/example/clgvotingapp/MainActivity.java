package com.example.clgvotingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    PhoneAuthCredential phoneAuthCredential;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String recievedotp="";
    EditText etphone,etotp;
    Button btsignup,btveri;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;

    public ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progess);
        db=FirebaseFirestore.getInstance();
        progress = new ProgressDialog(MainActivity.this);

        startFirebaseLogin();
        etphone=findViewById(R.id.etphone);
        etotp=findViewById(R.id.etotp);
        btveri=findViewById(R.id.btveri);
        btsignup=findViewById(R.id.btsignup);

        btsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Sending OTP");
                // progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                // progress.setIndeterminate(true);
                // progress.setProgress(0);
                progress.show();


                sendCode();














            }
        });
        btveri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String code=etotp.getText().toString().trim();
                if(code.isEmpty()||code.length()<6)
                {
                    etotp.setError("Enter code....");
                    etotp.requestFocus();
                    return;
                }
                ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("OTP Verification");
                // progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //   progress.setIndeterminate(true);
                //   progress.setProgress(0);


                // sendCode();
                verification(code);

            }
        });

    }
    public void sendCode()
    {
        String ph="+91"+etphone.getText().toString();
        //final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Map<String, String> userMap = new HashMap<>();
        userMap.get("Register no");



        if(ph.isEmpty())
        {
            etphone.setError("phone no is required:");
            etphone.requestFocus();
        }
        if(ph.length()<10)
        {
            etphone.setError("please enter valid number:");
            etphone.requestFocus();
        }
        progress.show();
        // pro
        // gressBar.setVisibility(View.VISIBLE);
        //if(!ph.equals(userMap.get("Register no"))){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                ph,      // Phone number to verify

                60,// Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                MainActivity.this,
                // Activity (for callback binding)
                mCallbacks);// OnVerificationStateChangedCallbacks

        //  }
      /*  else
        {
            Toast.makeText(MainActivity.this,"User already registered",Toast.LENGTH_LONG).show();
        }
*/

    }


    public void verification(String code) {
        String enterOtp = etotp.getText().toString();
        phoneAuthCredential = PhoneAuthProvider.getCredential(recievedotp,code);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,"Otp Verified",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                    startActivity(intent);
                    // TODO: show loading while we fetches user data
                 /*  String uid = mAuth.getCurrentUser().getUid();
                    Task<DocumentSnapshot> docRef = db.collection("users").document(uid).get();
                   DocumentSnapshot document= docRef.getResult();


                  fetch();


                 // Task<DocumentSnapshot> future = db.collection("users").document(uid).get();
                   //DocumentSnapshot document = (DocumentSnapshot) task.getResult();


                   if(document.exists()) {
                        Intent intent=new Intent(MainActivity.this,SecondActiviy.class);
                        startActivity(intent);

                        // TODO: user exits dont redirect to xyz screen
                    }


                  else {
                        // TODO: redirect to registration activity
                        Intent intent=new Intent(MainActivity.this,VerificationActivity.class);
                        startActivity(intent);
                    }*/

//                    Intent intent=new Intent(MainActivity.this,SecondActiviy.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                 //  fetch();
                }

                else
                {
                    Toast.makeText(MainActivity.this,"Otp Not Verified",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void startFirebaseLogin()
    {
        mAuth=FirebaseAuth.getInstance();

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code=phoneAuthCredential.getSmsCode();
                if(code!=null){
                    etotp.setText(code);
                    verification(code);
                    progress.dismiss();
                }


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {


            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                recievedotp=s;
                progress.hide();

            }
        };



    }

    private void fetch()
    {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>( ) {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // documentSnapshot.getId();
                if(documentSnapshot.exists())
                {
                    Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(MainActivity.this,VerificationNevi.class);
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(new OnFailureListener( ) {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    // @Override
   /* protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {


            Intent intent = new Intent(MainActivity.this, SecondActiviy.class);
            startActivity(intent);
            //   }
        }


    }*/
}
