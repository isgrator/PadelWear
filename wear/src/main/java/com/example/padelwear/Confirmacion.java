package com.example.padelwear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Confirmacion extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmacion);

        //Confirmación con cuenta atrás *******************
        ImageButton aceptar =  (ImageButton) findViewById(R.id.aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startActivityForResult(new Intent(Confirmacion.this,CuentaAtras.class), 9);
            }
        });
        ImageButton cancelar =  (ImageButton) findViewById(R.id.cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
            }
        });
        //*************************************************
    }

    //Cuando regresamos a Confirmacion se ejecuta este método (Cuenta atrás)
    @Override protected void onActivityResult(int requestCode, int resultCode,
                                              Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
        if (resultCode == RESULT_CANCELED) {
            //Toast.makeText(this,"Accion cancelada",Toast.LENGTH_SHORT).show();
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Operación cancelada");
        } else if(resultCode == RESULT_OK){
            //Toast.makeText(this,"Accion aceptada",Toast.LENGTH_SHORT).show();
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.SUCCESS_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Operación aceptada");
            //Guardamos datos de partida
            finish();
        }
        startActivity(intent);
    }
}
