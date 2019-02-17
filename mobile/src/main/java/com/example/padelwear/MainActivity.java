package com.example.padelwear;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    //NOTIFICACIONES*************************************************
    NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;

    //Apilar notificaciones
    final static String MI_GRUPO_DE_NOTIFIC = "mi_grupo_de_notific";

    //Notificación por voz
    public static final String EXTRA_RESPUESTA_POR_VOZ = "extra_respuesta_por_voz";

    //Notificacion por voz con anuncio broadcast
    public static final String EXTRA_MESSAGE="com.example.notificaciones.EXTRA_MESSAGE";
    public static final String ACTION_DEMAND="com.example.notificaciones.ACTION_DEMAND";
    //***************************************************************

    private static final String WEAR_MANDAR_TEXTO = "/mandar_texto";
    private static final String WEAR_ARRANCAR_ACTIVIDAD="/arrancar_actividad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //DEL PROYECTO NOTIFICACIONES ********************************************
        //Miramos si hemos recibido una respuesta por voz
        Bundle respuesta = RemoteInput.getResultsFromIntent(getIntent());
        if (respuesta!=null) {
            CharSequence texto = respuesta.getCharSequence(
                    EXTRA_RESPUESTA_POR_VOZ);
            ((TextView) findViewById(R.id.textViewRespuesta)).setText(texto);
        }

        //Crea notificacion
        notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 100, 300, 100});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Button wearButton = (Button)findViewById(R.id.boton1);
        wearButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //Añadir acciones a la notificacion
                Intent intencionLlamar = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:555123456"));
                PendingIntent intencionPendienteLlamar =
                        PendingIntent.getActivity(MainActivity.this, 0, intencionLlamar,0);

                //Añadir acción abrir en teléfono a la notificación
                // Creamos intención pendiente
                //Intent intencionMapa = new Intent(MainActivity.this, MainActivity.class);
                Intent intencionMapa = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=universidad+politecnica+valencia"));
                PendingIntent intencionPendienteMapa =
                        PendingIntent.getActivity(MainActivity.this, 0, intencionMapa, 0);
                //Añade acciones específicas
                NotificationCompat.Action accion =
                        new NotificationCompat.Action.Builder(R.mipmap.ic_action_call,
                                "llamar Wear", intencionPendienteLlamar).build();

                //Creamos una lista de acciones
                List<NotificationCompat.Action> acciones =
                        new ArrayList<NotificationCompat.Action>();
                acciones.add(accion);
                acciones.add(new NotificationCompat.Action(R.mipmap.ic_action_locate,
                        "Ver mapa", intencionPendienteMapa));

                String s="Texto largo con descripción detallada de la notificación. ";



                //Creamos una lista de páginas
                List<Notification> paginas = new ArrayList<Notification>();

                // Creamos un BigTextStyle para la segunda página
                NotificationCompat.BigTextStyle segundaPg = new NotificationCompat.BigTextStyle();
                segundaPg.setBigContentTitle("Página 2")
                        .bigText("Más texto.");
                // Creamos una notification para la segunda página
                Notification notificacionPg2 = new NotificationCompat.Builder(
                        MainActivity.this)
                        .setStyle(segundaPg)
                        .build();

                //********************************************
                // Creamos un BigTextStyle para la tercera página
                NotificationCompat.BigTextStyle terceraPg = new NotificationCompat.BigTextStyle();
                segundaPg.setBigContentTitle("Página 3")
                        .bigText("Mucho más texto.");
                // Creamos una notification para la tercera página
                Notification notificacionPg3 = new NotificationCompat.Builder(
                        MainActivity.this)
                        .setStyle(terceraPg)
                        .build();
                //********************************************


                // Creamos un WearableExtender para añadir funcionalidades para wearable
                NotificationCompat.WearableExtender wearableExtender =
                        new NotificationCompat.WearableExtender()
                                .setHintHideIcon(true)
                                .setBackground(BitmapFactory.decodeResource(
                                        getResources(), R.drawable.escudo_upv))
                                .addActions(acciones)
                                .addPages(paginas);
                //.addPage(notificacionPg2);

                NotificationCompat.Builder notificacion =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Título")
                                .setContentText(Html.fromHtml("<b>Notificación</b> <u>Android<i>Wear</i></u>"))
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(s+s+s+s))
                                .setContentIntent(intencionPendienteMapa)
                                .addAction(R.mipmap.ic_action_call, "llamar", intencionPendienteLlamar)
                                .extend(new NotificationCompat.WearableExtender().addActions(acciones))
                                .extend(wearableExtender)
                                .setGroup(MI_GRUPO_DE_NOTIFIC);
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.escudo_upv));
                //.setContentText("Notificación Wear OS");

                notificationManager.notify(NOTIFICACION_ID, notificacion.build());

                //Apilar notificaciones
                int idNotificacion2 = 002;
                NotificationCompat.Builder notificacion2 =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setContentTitle("Nueva Conferencia")
                                .setContentText("Los neutrinos")
                                .setSmallIcon(R.mipmap.ic_action_mail_add)
                                .setGroup(MI_GRUPO_DE_NOTIFIC);
                notificationManager.notify(idNotificacion2, notificacion2.build());

                //Notificación resumen
                // Creamos una notificacion resumen
                int idNotificacion3 = 003;
                NotificationCompat.Builder notificacion3 =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setContentTitle("2 notificaciones UPV")
                                .setSmallIcon(R.mipmap.ic_action_attach)
                                .setLargeIcon(BitmapFactory.decodeResource(
                                        getResources(), R.drawable.escudo_upv))
                                .setStyle(new NotificationCompat.InboxStyle()
                                        .addLine("Nueva Conferencia   Los neutrinos")
                                        .addLine("Nuevo curso   Wear OS")
                                        .setBigContentTitle("2 notificaciones UPV")
                                        .setSummaryText("info@upv.es"))
                                .setNumber(2)
                                .setGroup(MI_GRUPO_DE_NOTIFIC)
                                .setGroupSummary(true);
                notificationManager.notify(idNotificacion3, notificacion3.build());
            }
        });

        //Notificación por voz *********************************************************
        Button butonVoz = (Button)findViewById(R.id.boton_voz);
        butonVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos una intención de respuesta
                Intent intencion = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent intencionPendiente =
                        PendingIntent.getActivity(MainActivity.this, 0, intencion,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                // Creamos la entrada remota para añadirla a la acción
                String[] opcRespuesta = getResources().getStringArray(R.array.opciones_respuesta);
                RemoteInput entradaRemota = new RemoteInput.Builder(
                        EXTRA_RESPUESTA_POR_VOZ)
                        .setLabel("respuesta por voz")
                        .setChoices(opcRespuesta)
                        .build();
                // Creamos la acción
                NotificationCompat.Action accion =
                        new NotificationCompat.Action.Builder(
                                android.R.drawable.ic_menu_set_as,
                                "responder", intencionPendiente)
                                .addRemoteInput(entradaRemota)
                                .build();
                // Creamos la notificación
                int idNotificacion = 002;
                NotificationCompat.Builder notificacion4 =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Respuesta por Voz")
                                .setContentText("Indica una respuesta")
                                .extend(new NotificationCompat.WearableExtender()
                                        .addAction(accion));
                // Lanzamos la notificación
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(MainActivity.this);
                notificationManager.notify(idNotificacion, notificacion4.build());
            }
        });
        //******************************************************************************

        //************************************************************************

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.accion_contador) {
            startActivity(new Intent(this, Contador.class));
            mandarMensaje(WEAR_ARRANCAR_ACTIVIDAD, "");
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                                Wearable.getMessageClient(getApplicationContext()).sendMessage(nodo.getId(),    path   ,texto.getBytes());
                        //Verificar si se ha enviado el mensaje
                        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
                            @Override public void onSuccess(Integer i) {
                                //Toast.makeText(getApplicationContext(), "enviado", Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Contador arrancado en el reloj", Toast.LENGTH_LONG).show();
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

}
