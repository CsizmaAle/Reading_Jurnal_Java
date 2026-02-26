package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Jurnal;

import java.util.ArrayList;

/**
 * Clasa utilitara pentru Clasa Jurnal
 */
public class UtilitaraJurnal {

    /**
     * Metoda pentru afisarea unei liste de tip jurnal
     * @param jurnal {@code ArrayList<Jurnal>}; lista de intrari in jurnalul de lectura
     */
    public static void afisare(ArrayList<Jurnal> jurnal) {
        for(Jurnal j : jurnal) {
            System.out.println(j.toString());
        }
    }
}
