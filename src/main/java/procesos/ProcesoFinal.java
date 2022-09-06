package main.java.procesos;

import main.java.buzones.Buzon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcesoFinal extends ProcesoExtremo {
    private final Buzon BUZON_ENTRADA;
    private volatile int mensajesFinalesRecibidos;

    public ProcesoFinal(Buzon buzonEntrada) {
        mensajes = new ArrayList<>();
        BUZON_ENTRADA = buzonEntrada;
        mensajesFinalesRecibidos = 0;
    }

    @Override
    public void run() {
        while (mensajesFinalesRecibidos < NUM_FIN) {
            consumirMensaje();
        }
    }

    public String reintegrar(){
        return mensajes.stream().filter(m -> !(m.contains("FIN")))
                .reduce("", String::concat);
    }

    private void consumirMensaje(){
        while (!BUZON_ENTRADA.hayMensajes()){
            Thread.yield();
        }
        Optional<String> mensaje = BUZON_ENTRADA.removerMensajeF();
        if (mensaje.isPresent()){
            //System.out.println("Mensaje presente -> " + mensaje.get());
            if (mensaje.get().contains("FIN")){
                mensajesFinalesRecibidos++;
                //System.out.println(String.format("Proceso final: mensaje final número %d recibido", mensajesFinalesRecibidos));
            }
            else { mensajes.add(mensaje.get()); }
        }
    }

    @Override
    public String toString(){
        return String.format("ProcesoFinal{BuzónEntrada:%s}", BUZON_ENTRADA.toString());
    }

}
