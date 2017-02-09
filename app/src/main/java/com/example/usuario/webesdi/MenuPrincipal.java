package com.example.usuario.webesdi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuPrincipal extends AppCompatActivity {
    Button btnpaginaweb;
    Button btnFormulario;
    String nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        Intent Mainact = getIntent();
        String nombre = Mainact.getStringExtra("email");

        btnpaginaweb = (Button) findViewById(R.id.brnpaginaweb);
        btnFormulario = (Button) findViewById(R.id.btnFormulario);

        btnpaginaweb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                lanzarActivity();

                }
        });
        btnFormulario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                lanzarActivity2();

            }
        });

    }
    private void lanzarActivity(){
        Intent intent = new Intent(MenuPrincipal.this,PaginaWeb.class);
        startActivity(intent);
    }
    private void lanzarActivity2(){
        Intent intent = new Intent(MenuPrincipal.this,Formulario.class);
        intent.putExtra("nombre",nombre );
        startActivity(intent);
    }


}
