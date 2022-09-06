package main.java.procesos;

import java.util.List;

public abstract class ProcesoExtremo extends Thread{
    protected List<String> mensajes;
    protected volatile static int NUM_FIN;
    public List<String> getMensajes(){
        return mensajes;
    }
    public static void setNumFin(int numFin){
        NUM_FIN = numFin;
    }
}
