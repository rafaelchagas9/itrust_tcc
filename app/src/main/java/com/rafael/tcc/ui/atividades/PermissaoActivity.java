package com.rafael.tcc.ui.atividades;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rafael.tcc.R;

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
        });
    }
}
