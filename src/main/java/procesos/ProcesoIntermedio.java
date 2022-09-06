package main.java.procesos;



import main.java.buzones.Buzon;

import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ProcesoIntermedio extends Thread{
    private String mensaje;
    private final int NUM_PROCESO;
    private final int NIV_TRANSFORMACION;
    private final Buzon BUZON_SALIDA;
    private final Buzon BUZON_ENTRADA;
    private static Optional<CyclicBarrier> BARRERA = Optional.empty();
    private volatile boolean fin;

    public ProcesoIntermedio(int numeroProceso, int nivelTransformacion, Buzon buzonEntrada, Buzon buzonSalida) {
        NUM_PROCESO = numeroProceso;
        NIV_TRANSFORMACION = nivelTransformacion;
        BUZON_SALIDA = buzonSalida;
        BUZON_ENTRADA = buzonEntrada;
        fin = false;
    }

    @Override
    public void run() {
        while (!fin){
            consumirMensaje();
            System.out.println(String.format("El proceso(%d, %d) está procesando el mensaje '%s'", NUM_PROCESO,
                    NIV_TRANSFORMACION, mensaje));
            if (!mensaje.equals("FIN")) transformarMensaje();
            else { fin = true; }
            enviarMensaje();
            System.out.println(String.format("El proceso(%d, %d) acaba de enviar el mensaje '%s'", NUM_PROCESO,
                    NIV_TRANSFORMACION, mensaje));
        }
        System.out.println(String.format("Proceso(%d, %d) terminado", NUM_PROCESO,
                NIV_TRANSFORMACION));
        BARRERA.ifPresent(B -> {
            try {
                B.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setBarrera(CyclicBarrier barrera){
        BARRERA = Optional.of(barrera);
    }

    private void enviarMensaje(){
        BUZON_SALIDA.almacenarMensaje(mensaje);
    }

    private void consumirMensaje(){
        mensaje = BUZON_ENTRADA.removerMensaje();
    }

    private void transformarMensaje(){
        mensaje = String.format("%sT%d%d", mensaje,
                NIV_TRANSFORMACION, NUM_PROCESO);
        // Simular tiempo de procesamiento
        try {
            Thread.sleep(Long.parseLong("10"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString(){
        return String.format("ProcesoIntermedio{num:%d, trans:%d, buzónEntrada:%s, buzónSalida:%s}", NUM_PROCESO,
                NIV_TRANSFORMACION, BUZON_ENTRADA.toString(), BUZON_SALIDA.toString());
    }

}
