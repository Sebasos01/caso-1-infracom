package main.java.buzones;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class Buzon {
    private final int ID;
    private final int CAPACIDAD;
    private final Queue<String> BUFFER;
    private volatile int esperandoParaSacar;
    private volatile int esperandoParaMeter;

    public Buzon(int id, int capacidad){
        ID = id;
        this.CAPACIDAD = capacidad;
        BUFFER = new LinkedList<>();
        esperandoParaMeter = 0;
        esperandoParaSacar = 0;
    }

    public synchronized void almacenarMensaje(String mensaje){
        while (BUFFER.size() >= CAPACIDAD){
            try {
                esperandoParaMeter++;
                //System.out.println(String.format("Esperando para meter %d del buzón %d", esperandoParaMeter, ID));
                wait();
                esperandoParaMeter--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        BUFFER.add(mensaje);
        if (esperandoParaSacar > 0) notifyAll();
    }

    public synchronized String removerMensaje(){
        while (BUFFER.size() == 0){
            try {
                esperandoParaSacar++;
                //System.out.println(String.format("Esperando para sacar %d del buzón %d",esperandoParaSacar, ID));
                wait();
                esperandoParaSacar--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (esperandoParaMeter > 0){
            notifyAll();
        }
        return BUFFER.poll();
    }

    public synchronized boolean almacenarMensajeI(String mensaje){
        boolean sePudoAlmacenar = false;
        if (BUFFER.size() < CAPACIDAD){
            BUFFER.add(mensaje);
            sePudoAlmacenar = true;
            //System.out.println(BUFFER);
            if (esperandoParaSacar > 0) notifyAll();
        }
        return sePudoAlmacenar;
    }

    public synchronized Optional<String> removerMensajeF(){
        //System.out.println(BUFFER);
        Optional<String> mensaje = Optional.empty();
        if (BUFFER.size() == 0){
            return mensaje;
        }
        if (esperandoParaMeter > 0) { notifyAll(); }
        return Optional.of(BUFFER.poll());
    }

    public synchronized boolean hayMensajes(){
        return BUFFER.size() > 0;
    }

    public synchronized boolean hayDisponibilidad(){
        return BUFFER.size() < CAPACIDAD;
    }

    @Override
    public String toString(){
        return String.format("Buzón{id:%s, capacidad:%d}", ID, CAPACIDAD);
    }

}
