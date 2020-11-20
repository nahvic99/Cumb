package com.NahVicDev.cumb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText Username, Password, email;
    Button Btn_rg;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        final TextView term = (TextView) findViewById(R.id.term);
        term.setMovementMethod(LinkMovementMethod.getInstance());



        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        this.setTitle("Registro");


        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        email = findViewById(R.id.Email);
        Btn_rg = findViewById(R.id.btn_rg);
        auth = FirebaseAuth.getInstance();

        Btn_rg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = Username.getText().toString();
                String txt_Email = email.getText().toString();
                String txt_password = Password.getText().toString();

                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_Email) || TextUtils.isEmpty(txt_password))
                {
                    Toast.makeText(RegisterActivity.this, "Se requiere completar todos los campos", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "La contraseña debe contener mas de 6 caracteres", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(txt_username, txt_Email, txt_password);
                }
            }
        });
    }



    private void register(final String username, String email, String password)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("search", username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterActivity.this, "No te puedes registrar con este email o contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
