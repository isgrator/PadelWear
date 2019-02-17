package com.example.padelwear;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.comun.DireccionesGestureDetector;
import com.example.comun.Partida;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class Contador extends Activity implements DataClient.OnDataChangedListener{
    private Partida partida;
    private TextView misPuntos, misJuegos, misSets,
            susPuntos, susJuegos, susSets;
    private Vibrator vibrador;
    private long[] vibrEntrada = {0l, 500};
    private long[] vibrDeshacer = {0l, 500, 500, 500};

    //Sincronizar tanteo de la partida desde el reloj al móvil*******************
    private static final String WEAR_PUNTUACION = "/puntuacion";
    private static final String KEY_MIS_PUNTOS="com.example.padel.key.mis_puntos";
    private static final String KEY_MIS_JUEGOS="com.example.padel.key.mis_juegos";
    private static final String KEY_MIS_SETS="com.example.padel.key.mis_sets";
    private static final String KEY_SUS_PUNTOS="com.example.padel.key.sus_puntos";
    private static final String KEY_SUS_JUEGOS="com.example.padel.key.sus_juegos";
    private static final String KEY_SUS_SETS="com.example.padel.key.sus_sets";
    //***************************************************************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contador);
        partida = new Partida();
        vibrador = (Vibrator) this.getSystemService(Context
                .VIBRATOR_SERVICE);
        misPuntos = (TextView) findViewById(R.id.misPuntos);
        susPuntos = (TextView) findViewById(R.id.susPuntos);
        misJuegos = (TextView) findViewById(R.id.misJuegos);
        susJuegos = (TextView) findViewById(R.id.susJuegos);
        misSets   = (TextView) findViewById(R.id.misSets);
        susSets   = (TextView) findViewById(R.id.susSets);
        actualizaNumeros();
        View fondo = findViewById(R.id.fondo);
        fondo.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(
                    Contador.this,
                    new DireccionesGestureDetector
                            .SimpleOnDireccionesGestureListener() {
                        @Override public boolean onArriba(MotionEvent e1,
                                                          MotionEvent e2, float distX, float distY) {
                            partida.rehacerPunto();
                            vibrador.vibrate(vibrDeshacer, -1);
                            actualizaNumeros();
                            return true;
                        }
                        @Override public boolean onAbajo(MotionEvent e1,
                                                         MotionEvent e2, float distX, float distY) {
                            partida.deshacerPunto();
                            vibrador.vibrate(vibrDeshacer, -1);
                            actualizaNumeros();
                            return true;
                        }
                    });
            @Override public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });
        misPuntos.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(
                    Contador.this,
                    new DireccionesGestureDetector
                            .SimpleOnDireccionesGestureListener() {
                        @Override public boolean onDerecha(MotionEvent e1,
                                                           MotionEvent e2, float distX, float distY) {
                            partida.puntoPara(true);
                            vibrador.vibrate(vibrEntrada, -1);
                            actualizaNumeros();
                            return true;
                        }
                    });
            @Override public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });
        susPuntos.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(
                    Contador.this,
                    new DireccionesGestureDetector
                            .SimpleOnDireccionesGestureListener() {
                        @Override public boolean onDerecha(MotionEvent e1,
                                                           MotionEvent e2, float distX, float distY) {
                            partida.puntoPara(false);
                            vibrador.vibrate(vibrEntrada, -1);
                            actualizaNumeros();
                            return true;
                        }
                    });
            @Override public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });

        //acceder al ítem de puntuación al arrancar la actividad e inicializar el valor del tanteo
        Task<DataItemBuffer> task = Wearable.getDataClient(getApplicationContext()).getDataItems();
        task.addOnCompleteListener(new OnCompleteListener<DataItemBuffer>() {
            @Override public void onComplete(@NonNull Task<DataItemBuffer> task) {
                for (DataItem dataItem: task.getResult()) {
                    if (dataItem.getUri().getPath().equals(WEAR_PUNTUACION)) {
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                        //contador = dataMapItem.getDataMap().getInt(KEY_CONTADOR);
                        partida.setMisPuntosByte(dataMapItem.getDataMap().getByte(KEY_MIS_PUNTOS));
                        partida.setMisJuegosByte(dataMapItem.getDataMap().getByte(KEY_MIS_JUEGOS));
                        partida.setMisSetsByte(dataMapItem.getDataMap().getByte(KEY_MIS_SETS));
                        partida.setSusPuntosByte(dataMapItem.getDataMap().getByte(KEY_SUS_PUNTOS));
                        partida.setSusJuegosByte(dataMapItem.getDataMap().getByte(KEY_SUS_JUEGOS));
                        partida.setSusSetsByte(dataMapItem.getDataMap().getByte(KEY_SUS_SETS));
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                actualizaNumeros();
                            }
                        });
                    }
                }
                task.getResult().release();
            }
        });

        //*******************************************************************************************
    }

    void actualizaNumeros() {
        misPuntos.setText(partida.getMisPuntos());
        susPuntos.setText(partida.getSusPuntos());
        misJuegos.setText(partida.getMisJuegos());
        susJuegos.setText(partida.getSusJuegos());
        misSets.setText(partida.getMisSets());
        susSets.setText(partida.getSusSets());
        sincronizaDatos();
    }

    //Sincronización de datos entre reloj y móvil***********************************
    private void sincronizaDatos() {
        Log.d("Padel Wear", "Sincronizando");
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(WEAR_PUNTUACION);
        putDataMapReq.getDataMap().putByte(KEY_MIS_PUNTOS, partida.getMisPuntosByte());
        putDataMapReq.getDataMap().putByte(KEY_MIS_JUEGOS, partida.getMisJuegosByte());
        putDataMapReq.getDataMap().putByte(KEY_MIS_SETS, partida.getMisSetsByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_PUNTOS, partida.getSusPuntosByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_JUEGOS, partida.getSusJuegosByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_SETS, partida.getSusSetsByte());

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.getDataClient(getApplicationContext()).putDataItem(putDataReq);
    }

    @Override public void onDataChanged(DataEventBuffer eventos) {
        for (DataEvent evento : eventos) {
            if (evento.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = evento.getDataItem();

                if (item.getUri().getPath().equals(WEAR_PUNTUACION)) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    partida.setMisPuntosByte(dataMap.getByte(KEY_MIS_PUNTOS));
                    partida.setMisJuegosByte(dataMap.getByte(KEY_MIS_JUEGOS));
                    partida.setMisSetsByte(dataMap.getByte(KEY_MIS_SETS));
                    partida.setSusPuntosByte(dataMap.getByte(KEY_SUS_PUNTOS));
                    partida.setSusJuegosByte(dataMap.getByte(KEY_SUS_JUEGOS));
                    partida.setSusSetsByte(dataMap.getByte(KEY_SUS_SETS));
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            actualizaNumeros();
                        }
                    });
                }

            } else if (evento.getType() == DataEvent.TYPE_DELETED) {
                // Algún ítem ha sido borrado
            }
        }
    }

    @Override protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }
    //************************************************************************************
}
