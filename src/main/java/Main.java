package main.java;

import main.java.buzones.Buzon;
import main.java.procesos.ProcesoExtremo;
import main.java.procesos.ProcesoFinal;
import main.java.procesos.ProcesoInicial;
import main.java.procesos.ProcesoIntermedio;
import main.java.util.Utiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        // Definición de variables importantes y de los buzones y procesos extremos
        System.out.print("Ingrese la capacidad de los buzones extremos: ");
        int capacidadExtremos = sc.nextInt(); // La capacidad de los buzones extremo
        System.out.print("Ingrese la capacidad de los buzones intermedios: ");
        int capacidadIntermedios = sc.nextInt(); // La capacidad de los buzones intermedios
        System.out.print("Ingrese el número de mensajes que debe producir el proceso inicial: ");
        int numMensajes = sc.nextInt(); // El número de mensajes que el proceso inicial debe crear
        int T = 3; // Número de transformaciones
        int P = 3; // Número de procesos por transformación
        ProcesoExtremo.setNumFin(P); // Establece el número de mensajes finales que se tienen que enviar
        ProcesoIntermedio.setBarrera(new CyclicBarrier(T * P, new Runnable() {
            @Override
            public void run() {
                System.out.println("Procesos intermedios terminados");
            }
        }));
        Buzon buzonInicial = new Buzon(1, capacidadExtremos);
        Buzon buzonFinal = new Buzon(8, capacidadExtremos);
        ProcesoInicial procesoInicial = new ProcesoInicial(numMensajes, buzonInicial);
        ProcesoFinal procesoFinal = new ProcesoFinal(buzonFinal);

        // Creación de los buzones intermedios y la estructura que ordena todos los buzones
        Buzon[][] buzones = new Buzon[P][T + 1];
        for (int i = 0; i < P; i++) {
            buzones[i][0] = buzonInicial;
            buzones[i][T] = buzonFinal;
            for (int j = 1; j < T; j++) {
                buzones[i][j] = new Buzon(i*(T-1) + j + 1, capacidadIntermedios);
            }
        }

        // Creación de los procesos intermedios y sus relaciones con los buzones y la estructura que los ordena
        Thread[] procesos = new Thread[P*T + 2];
        procesos[0] = procesoInicial;
        procesos[P*T + 1] = procesoFinal;
        for (int i = 0; i < P; i++) {
            for (int j = 0; j < T; j++) {
                ProcesoIntermedio procesoIntermedio = new ProcesoIntermedio(i + 1, j + 1,
                        buzones[i][j], buzones[i][j + 1]);
                procesos[j*P + i + 1] = procesoIntermedio;
            }
        }

        // Imprime los buzones que se crearon
        System.out.println("La lista de los buzones: ");
        List<List<Buzon>> buzonesAL = new ArrayList<>();
        for (int i = 0; i < buzones.length; i++) {
            buzonesAL.add(Arrays.asList(buzones[i]));
        }
        System.out.println(Arrays.asList(buzonesAL));

        // Imprime los procesos que se crearon
        System.out.println("La lista de los procesos: ");
        System.out.println(Arrays.asList(procesos));

        // Inicia los procesos, empiezan desde el proceso final (de reintegración) hasta el inicial (el que crea los mensajes)
        for (int i = (procesos.length - 1); i > -1; i--) {
            procesos[i].start();
        }

        // Esperamos a que termine el proceso final para proseguir
        try {
            procesoFinal.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Proceso final terminado");
        System.out.println("Reintegración: " + procesoFinal.reintegrar());
        boolean checkSum = Utiles.checkSum(procesoInicial.getMensajes(), procesoFinal.getMensajes());
        System.out.println("Llegaron todos los mensajes: " + checkSum);
    }
}
