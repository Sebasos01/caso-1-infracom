package main.java.procesos;


import main.java.buzones.Buzon;

import java.util.ArrayList;
import java.util.List;

public class ProcesoInicial extends ProcesoExtremo{
    private final int N;
    private volatile int mensajesEnviados;
    private volatile int mensajesFinalesEnviados;

    private final Buzon BUZON_SALIDA;

    public ProcesoInicial(int N, Buzon buzonSalida){
        mensajes = new ArrayList<>();
        this.N = N;
        BUZON_SALIDA = buzonSalida;
        mensajesEnviados = 0;
        mensajesFinalesEnviados = 0;
    }

    @Override
    public void run(){
        while (mensajesEnviados < N){
            String nuevoMensaje = "M"  + (mensajesEnviados + 1);
            enviarMensaje(nuevoMensaje);
        }
        while (mensajesFinalesEnviados < NUM_FIN){
            enviarMensajeFin();
        }
        System.out.println("Proceso inicial terminado");
    }

    public void enviarMensaje(String mensaje){
        while (true){
            while (!BUZON_SALIDA.hayDisponibilidad()){
                Thread.yield();
            }
            boolean sePudoAlmacenar = BUZON_SALIDA.almacenarMensajeI(mensaje);
            if (sePudoAlmacenar) {
                mensajes.add(mensaje);
                mensajesEnviados++;
                break;
                //System.out.println("Nuevo mensaje enviado ->" + nuevoMensaje);
            }
        }
    }

    public void enviarMensajeFin(){
        while (true) {
            while (!BUZON_SALIDA.hayDisponibilidad()){
                Thread.yield();
            }
            boolean sePudoAlmacenar = BUZON_SALIDA.almacenarMensajeI("FIN");
            if (sePudoAlmacenar) {
                mensajesFinalesEnviados++;
                //System.out.println("Mensaje de FIN enviado");
                break;
            }
        }
    }

    @Override
    public String toString(){
        return String.format("ProcesoInicial{BuzónSalida:%s}", BUZON_SALIDA.toString());
    }
}
