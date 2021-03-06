package com.example.comun;

import java.util.Vector;

public class Partida {
    private Vector<Puntuacion> puntos;
    private Vector<Puntuacion> partidaRehacer;

    public Partida() {
        puntos = new Vector<Puntuacion>();
        puntos.add(new Puntuacion());
        partidaRehacer = new Vector<Puntuacion>();
    }

    public void puntoPara(boolean miEquipo) {
        //vibr.vibrate(vibrEntrada, -1);
        puntos.add(new Puntuacion(puntos.lastElement(), miEquipo));

        //Log.e("Tag", historial.toString());
        partidaRehacer = new Vector<Puntuacion>();
        //actualizaNumeros();
    }

    public void deshacerPunto() {
        if (puntos.size() > 1) {
            //vibr.vibrate(vibrDeshacer, -1);
            partidaRehacer.add(puntos.lastElement());
            puntos.remove(puntos.size() - 1);
            //actualizaNumeros();
        } else {
            // vibr.vibrate(vibrError, -1);
        }
    }

    public void rehacerPunto() {

        if (partidaRehacer.size() > 0) {
            //vibr.vibrate(vibrDeshacer, -1);
            puntos.add(partidaRehacer.lastElement());
            partidaRehacer.remove(partidaRehacer.size() - 1);
            //actualizaNumeros();
        } else {
            //vibr.vibrate(vibrError, -1);
        }
    }

    public String getMisPuntos() {
        return Puntuacion.puntos[puntos.lastElement().getMiPuntuacion()];
    }

    public String getSusPuntos() {
        return Puntuacion.puntos[puntos.lastElement().getSuPuntuacion()];
    }

   /* public int getMisJuegos(){
        return partida.lastElement().getMisJuegos();
    }*/

    public String getMisJuegos() {
        return String.valueOf(puntos.lastElement().getMisJuegos());
    }

    public String getSusJuegos() {
        return String.valueOf(puntos.lastElement().getSusJuegos());
    }

    public String getMisSets(){
        return String.valueOf(puntos.lastElement().getMisSets());
    }

    public String getSusSets() {
        return String.valueOf(puntos.lastElement().getSusSets());
    }

    //Sincronización de datos entre reloj y móvil*******************************************
    public byte getMisPuntosByte(){
        return puntos.lastElement().getMiPuntuacion();
    }
    public byte getMisJuegosByte(){
        return puntos.lastElement().getMisJuegos();
    }
    public byte getMisSetsByte(){
        return puntos.lastElement().getMisSets();
    }
    public byte getSusPuntosByte(){
        return puntos.lastElement().getSuPuntuacion();
    }
    public byte getSusJuegosByte(){
        return puntos.lastElement().getSusJuegos();
    }
    public byte getSusSetsByte(){
        return puntos.lastElement().getSusSets();
    }

    ////////////////////////////////

    public void setMisPuntosByte(byte data){
        puntos.lastElement().setMiPuntuacion(data);
    }
    public void setMisJuegosByte(byte data){
        puntos.lastElement().setMisJuegos(data);
    }
    public void setMisSetsByte(byte data){
        puntos.lastElement().setMisSets(data);
    }
    public void setSusPuntosByte(byte data){
        puntos.lastElement().setSuPuntuacion(data);
    }
    public void setSusJuegosByte(byte data){
        puntos.lastElement().setSusJuegos(data);
    }
    public void setSusSetsByte(byte data){
        puntos.lastElement().setSusSets(data);
    }

    //**************************************************************************************
}
