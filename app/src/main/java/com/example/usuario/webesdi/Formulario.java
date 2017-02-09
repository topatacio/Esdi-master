package com.example.usuario.webesdi;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Formulario extends AppCompatActivity {
    EditText etxtBody;
    EditText etxtAsunto;
    String nombre ;
    Button btnEnviar;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        Intent Menuintent = getIntent();
        nombre = Menuintent.getStringExtra("email");

        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        etxtBody = (EditText) findViewById(R.id.etxtBody);
        etxtAsunto = (EditText) findViewById(R.id.etxtAsunto);


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Mail m = new Mail("oscergar@gmail.com", "kirowser");

                String[] toArr = {"oscergar@gmail.com"};
                m.set_to(toArr);
                m.set_from("sugerencia");
                String asunto = etxtAsunto.getText().toString();
                m.set_subject(asunto);
                String bodyy = etxtBody.getText().toString();
                m.setBody("bodyy");

                try {
                    if(m.send()) {
                        Toast.makeText(getApplicationContext(), "Email was sent successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email was not sent.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e) {
                    //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                    Log.e("MailApp", "Could not send email", e);
                }
            }
        });
    }

}
