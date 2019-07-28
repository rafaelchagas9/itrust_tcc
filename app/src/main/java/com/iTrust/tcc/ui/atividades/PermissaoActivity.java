package com.iTrust.tcc.ui.atividades;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.iTrust.tcc.R;


public class PermissaoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissao);



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(PermissaoActivity.this, MainActivity.class));
            finish();
        }

        Button btnPermitir = findViewById(R.id.btn_permitir);
        btnPermitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkMapServices()){
                    Dexter.withActivity(PermissaoActivity.this)
                            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    startActivity(new Intent(PermissaoActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    if (response.isPermanentlyDenied()){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PermissaoActivity.this);
                                        builder.setTitle("Permissão negada")
                                                .setMessage("A permissão de acesso a localização do aparelho foi negada permanentemente. " +
                                                        "Por favor, habilite a permissão nas configurações do dispositivo")
                                                .setNegativeButton("Cancelar", null)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent();
                                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                        intent.setData(Uri.fromParts("packge", getPackageName(), null));
                                                    }
                                                })
                                                .show();
                                    }
                                    Toast.makeText(PermissaoActivity.this, "Permissão negada", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .check();
                }
            }
        });
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage("Esse aplicativo necessita do GPS para funcionar corretamente, você deseja habilita-lo?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, 9003);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private boolean isServicesOK(){
        Log.d("Permissão Activity", "Checando versão do google play services");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PermissaoActivity.this);

        if (available == ConnectionResult.SUCCESS){
            Log.d("Permissão Activity", "Google play services está instalado e atualizado");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d("Permissão Activity", "Google play services está com algum problema, mas é possível arruma");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PermissaoActivity.this, available, 9001);
            dialog.show();
        }else{
            Toast.makeText(this, "Por favor, atualize ou instale a Google Play Services para continuar", Toast.LENGTH_SHORT).show();
        }
        return  false;
    }
}
