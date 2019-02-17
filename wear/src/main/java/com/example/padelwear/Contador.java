package com.example.padelwear;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.comun.DireccionesGestureDetector;
import com.example.comun.Partida;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Contador extends WearableActivity {
    private Partida partida;
    private TextView misPuntos, misJuegos, misSets,
            susPuntos, susJuegos, susSets, hora;
    private Vibrator vibrador;
    private long[] vibrEntrada = {0l, 500};
    private long[] vibrDeshacer = {0l, 500, 500, 500};

    private DismissOverlayView dismissOverlay;
    private GestureDetector detector;

    //Sincronizar tanteo de la partida desde el reloj al móvil*******************
    private static final String WEAR_PUNTUACION = "/puntuacion";
    private static final String KEY_MIS_PUNTOS="com.example.padel.key.mis_puntos";
    private static final String KEY_MIS_JUEGOS="com.example.padel.key.mis_juegos";
    private static final String KEY_MIS_SETS="com.example.padel.key.mis_sets";
    private static final String KEY_SUS_PUNTOS="com.example.padel.key.sus_puntos";
    private static final String KEY_SUS_JUEGOS="com.example.padel.key.sus_juegos";
    private static final String KEY_SUS_SETS="com.example.padel.key.sus_sets";
    //***************************************************************************

    //Modo ambiente****************************************************************************
    private Typeface fuenteNormal = Typeface.create("sans-serif", Typeface.NORMAL);
    private Typeface fuenteFina = Typeface.create("sans-serif-thin", Typeface.NORMAL);
    BroadcastReceiver _broadcastReceiver;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm");
    //*****************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contador);

        setAmbientEnabled();

        partida = new Partida();
        vibrador = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        hora = findViewById(R.id.hora);
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

                        @Override
                        public void onLongPress(MotionEvent e) { dismissOverlay.show(); }
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
                        @Override
                        public void onLongPress(MotionEvent e) { dismissOverlay.show(); }
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
                        @Override
                        public void onLongPress(MotionEvent e) { dismissOverlay.show(); }
                    });
            @Override public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });

        //*********************************
        /*SwipeDismissFrameLayout root =
                (SwipeDismissFrameLayout) findViewById(R.id.swipe_dismiss_root);
        root.addCallback(new SwipeDismissFrameLayout.Callback() {
                             @Override public void onDismissed(SwipeDismissFrameLayout layout) {
                                 Contador.this.finish();
                             }
                         }
        );*/
        //*********************************

        //Crear una actividad para llevar el conteo de la partida*************

        dismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        dismissOverlay.setIntroText(
                "Para salir de la aplicación, haz una pulsación larga");
        dismissOverlay.showIntroIfNecessary();


        // Configuramos el detector de pulsaciones
        detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    public void onLongPress(MotionEvent evento) {
                        dismissOverlay.show();
                    }
                });

        //Evitar que entre en suspensión tras unos segundos
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    // Detectar pulsaciones largas
    @Override
    public boolean onTouchEvent(MotionEvent evento) {
        return detector.onTouchEvent(evento) || super.onTouchEvent(evento);
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

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        /*Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        hora.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));*/
        hora.setText(_sdfWatchTime.format(new Date()));
        hora.setVisibility(View.VISIBLE);

        misPuntos.setTypeface(fuenteFina);
        misPuntos.getPaint().setAntiAlias(false);

        susPuntos.setTypeface(fuenteFina);
        susPuntos.getPaint().setAntiAlias(false);

        misJuegos.setTypeface(fuenteFina);
        misJuegos.getPaint().setAntiAlias(false);

        susJuegos.setTypeface(fuenteFina);
        susJuegos.getPaint().setAntiAlias(false);

        misSets.setTypeface(fuenteFina);
        misSets.getPaint().setAntiAlias(false);

        susSets.setTypeface(fuenteFina);
        susSets.getPaint().setAntiAlias(false);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        hora.setVisibility(View.GONE);

        misPuntos.setTypeface(fuenteNormal);
        misPuntos.getPaint().setAntiAlias(true);

        susPuntos.setTypeface(fuenteNormal);
        susPuntos.getPaint().setAntiAlias(true);

        misJuegos.setTypeface(fuenteNormal);
        misJuegos.getPaint().setAntiAlias(true);

        susJuegos.setTypeface(fuenteNormal);
        susJuegos.getPaint().setAntiAlias(true);

        misSets.setTypeface(fuenteNormal);
        misSets.getPaint().setAntiAlias(true);

        susSets.setTypeface(fuenteNormal);
        susSets.getPaint().setAntiAlias(true);
    }

    //Para actualizar la hora en modo ambiente
    @Override
    public void onStart() {
        super.onStart();
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    hora.setText(_sdfWatchTime.format(new Date()));
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

    // Sincronizar tanteo de la partida desde el reloj al móvil ***************
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

    /*
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
    }*/
    //************************************************************************
}
