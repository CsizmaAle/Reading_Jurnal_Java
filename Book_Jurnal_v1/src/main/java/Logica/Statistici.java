package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Status;

import java.util.ArrayList;

/**
 * Clasa ce defineste statisticile aplicatiei
 */
public class Statistici {
    /**
     * Metoda ce returneaza numarul total de carti din biblioteca
     * @param carti {@code ArrayList<Carte>}; losta cartilor din biblioteca
     * @return int; numarul de carti din biblioteca
     */
    public static int numarTotalCarti(ArrayList<Carte> carti){
        return carti.size();
    }

    /**
     * Metoda ce numara cate carti din biblioteca sunt citite
     * @param carti {@code ArrayList<Carte>}; lista cartilor din biblioteca
     * @return int; numarul de carti din biblioteca
     */
    public static int numarCartiCitite(ArrayList<Carte> carti){
        int contor=0;
        for(Carte c : carti){
            if(c.getStatus()== Status.FINALIZATA)contor++;
        }
        return contor;
    }


}
