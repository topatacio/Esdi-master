package com.example.usuario.webesdi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
//aqui hay que añadir un if, que mire el id de la cuenta y active  un activity o otro dependiendo.

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    //probando
    //probando 2

    private SignInButton btnSignIn;
    private Button btnSignOut;
    private Button btnRevoke;
    private Button btnEntrar;
    private TextView txtNombre;
    private TextView txtEmail;

    private GoogleApiClient apiClient;
    private static final int RC_SIGN_IN = 1001;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEntrar = (Button)findViewById(R.id.btnEntrar);
        btnSignIn = (SignInButton)findViewById(R.id.sign_in_button);
        btnSignOut = (Button)findViewById(R.id.sign_out_button);
        btnRevoke = (Button)findViewById(R.id.revoke_button);
        txtNombre = (TextView)findViewById(R.id.txtNombre);
        txtEmail = (TextView)findViewById(R.id.txtEmail);

        //Google API Client
        //Definimos que informacion queremos recuperar del usuario que se identifique
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
        //Instanciamos el cliente donde logeara el usuario, decimos que lo manege Google de manera
        //automatica y le enviamos los parametros de arriba para que nos devuelva info
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Personalización del botón de login(css)

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setColorScheme(SignInButton.COLOR_LIGHT);
        btnSignIn.setScopes(gso.getScopeArray());

        //Eventos de los botones, le decimos al boton de login que nos inicie una actividad dlogeo de google.
        // el "start ActivityForResult" nos devuelve el resultado de comprovaciones de seguridad, que podemos gestionar en onActivityResult().
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);


            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                updateUI(false);
                            }
                        });
            }
        });

        btnRevoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.revokeAccess(apiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                updateUI(false);
                            }
                        });
            }
        });

        updateUI(false);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                iniciarActivity();
            }
        });
    }
    //mira el Id de la cuenta logeada, y comprueba si es de una lista.
     void iniciarActivity(){
        Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
         String mensaje = txtEmail.getText().toString();
         intent.putExtra("email",mensaje );
        startActivity(intent);
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexion!", Toast.LENGTH_SHORT).show();
        Log.e("GoogleSignIn", "OnConnectionFailed: " + connectionResult);
    }
    // nos llega el resultado del login, le aplicamos getSignInResultFromIntent, y iniciamos un metodo para gestionarlo.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSignInResult(result);
        }
    }
    //Miramos si la conexion ha salido bien con .isSucces, si es que si, guardamos la cuenta logeada
    // en un GoogleSignInAccount, y utilizamos su datos para actualizar textos de la interfaz,
    // en ambos casos actualiamos la UI, para que se adapte al estado del usuario
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            //Usuario logueado --> Mostramos sus datos
            GoogleSignInAccount acct = result.getSignInAccount();
            txtNombre.setText(acct.getDisplayName());
            txtEmail.setText(acct.getEmail());
            updateUI(true);


        } else {
            //Usuario no logueado --> Lo mostramos como "Desconectado"
            updateUI(false);
        }
    }
    // Enseña los botones que interesan para el estado del usuario(si ya ha logeado no le enseñamos el SignIn)
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevoke.setVisibility(View.VISIBLE);
        } else {
            txtNombre.setText("Desconectado");
            txtEmail.setText("Desconectado");

            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevoke.setVisibility(View.GONE);
        }
    }
    //Todo este codigo en onStart se encarga de logear al usuario si ya ha logeado anteriormente,
    // sin tener que hacer click en Sign In de nuevo
    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(apiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Silent SignI-In");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
