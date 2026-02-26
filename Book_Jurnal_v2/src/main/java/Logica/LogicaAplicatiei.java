package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Status;

import java.time.LocalDate;

/**
 * Clasa ce contine metode esentiale pentru anumite actiunii ale logicii aplicatiei
 */
public class LogicaAplicatiei {

    /**
     * Metoda ce schimba informatii legate de carte dupa terminare ei: scor, status, procent,dataFinish, scor, review
     * @param c Carte; cartea care a fost terminata de citit
     *
     */
    public static void finishReading(Carte c, Double score, String review) {
        c.setProcent(1);
        c.setDataFinish(LocalDate.now());
        c.setStatus(Status.FINALIZATA);
        if (score != null) {
            c.setScor(score);
        }
        if (review != null) {
            c.setReview(review);
        }
    }

    /**
     * Metoda care schimba informatiile despre o carte pentru care se incepe procesul de citire
     * @param c Carte; cartea care se incepe pentru a fi citita
     */
    public static void startReading(Carte c) {
        c.setStatus(Status.IN_CURS_DE_CITIRE);
        c.setDataStart(LocalDate.now());
    }

    /**
     * Metoda ce schimba statusul unei carti in ABANDONATA
     * @param c Carte; un obiect de tip carte al carui status se doreste a fi schimbat
     */
    public static void abandonReading(Carte c) {
        c.setStatus(Status.ABANDONATA);
    }

}
