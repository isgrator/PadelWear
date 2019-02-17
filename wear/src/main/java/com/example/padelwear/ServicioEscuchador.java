package com.example.padelwear;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

//Servicio que está siempre verificando la llegada de un mensaje

public class ServicioEscuchador extends WearableListenerService {
    private static final String WEAR_ARRANCAR_ACTIVIDAD = "/arrancar_actividad";

    @Override public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase(
                WEAR_ARRANCAR_ACTIVIDAD)) {
            Intent intent = new Intent(this, Contador.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            Log.i("isabel","EMPIEZA ACTIVIDAD EN EL RELOJ");
        } else {
            Log.i("isabel","EL MENSAJE NO HA SIDO WEAR_ARRANCAR_ACTIVIDAD");
            super.onMessageReceived(messageEvent);

        }
    }
}



