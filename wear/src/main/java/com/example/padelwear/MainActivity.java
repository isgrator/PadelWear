package com.example.padelwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity {

    private static final String WEAR_MANDAR_TEXTO = "/mandar_texto";
    private static final String WEAR_ARRANCAR_ACTIVIDAD="/arrancar_actividad";

    // Elementos a mostrar en la lista
    String[] elementos = {"Partida", "Terminar partida", "Historial",
            "Jugadores", "Pasos", "Pulsaciones", "Terminar partida" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setAmbientEnabled();

        WearableRecyclerView lista = (WearableRecyclerView)
                findViewById(R.id.lista);
        //Layouts curvos*********************
        lista.setEdgeItemsCenteringEnabled(true);
        //***********************************
        lista.setLayoutManager(new WearableLinearLayoutManager(this,new CustomLayoutCallback()));
        Adaptador adaptador = new Adaptador(this, elementos);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer tag = (Integer) v.getTag();
                //Toast.makeText(MainActivity.this, "Elegida opción:" + tag, Toast.LENGTH_SHORT).show();
                switch (tag) {
                    case 0:
                        mandarMensaje(WEAR_ARRANCAR_ACTIVIDAD, "");
                        startActivity(new Intent(MainActivity.this, Contador.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, Confirmacion.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, Historial.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, Jugadores.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, Pasos.class));
                        break;
                }
            }
        });
        lista.setAdapter(adaptador);

        //Gesto de scroll circular
        lista.setCircularScrollingGestureEnabled(true);
        lista.setScrollDegreesPerScreen(180);
        lista.setBezelFraction(1.0f);
    }

    //Métodos**********************************************************************
    private void mandarMensaje(final String path, final String texto) {
        new Thread(new Runnable() {
            @Override public void run() {
                List<Node> nodos = null;
                try {
                    nodos = Tasks.await(Wearable.getNodeClient(
                            getApplicationContext()).getConnectedNodes()); //Obtenemos la lista de todos los nodos conectados
                    /*
                        Con await convertimos la llamada en síncrona, así bloquea el hilo actual hasta obtener una respuesta
                     */
                    for (Node nodo : nodos) {
                        Task<Integer> task =
                                //Wearable.getMessageClient(getApplicationContext()).sendMessage(nodo.getId(),WEAR_MANDAR_TEXTO,texto.getBytes());
                                Wearable.getMessageClient(getApplicationContext()).sendMessage(nodo.getId(),  path,  texto.getBytes());
                        //Verificar si se ha enviado el mensaje
                        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
                            @Override public void onSuccess(Integer i) {
                                //Toast.makeText(getApplicationContext(), "enviado", Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Contador arrancado en el móvil", Toast.LENGTH_LONG).show();
                            }
                        });
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override public void onFailure(Exception e) {
                                Toast.makeText(getApplicationContext(), "Error :" + e,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (ExecutionException e) {
                    Log.e("Sincronización Wear", e.toString());
                } catch (InterruptedException e) {
                    Log.e("Sincronización Wear", e.toString());
                }
            }
        }).start();
    }
    //*****************************************************************************
}
