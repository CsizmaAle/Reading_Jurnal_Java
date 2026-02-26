package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Clasa utilitara pentru clasa carte
 */
public class UtilitaraCarte {

    /**
     * Metoda pentru afisarea unei liste de carti
     * @param carti {@code ArrayList<Carte>}; o litsa de carti care se doreste a fi afisata
     */
    public static void afisare(ArrayList<Carte> carti) {
        for(Carte c : carti) {
            System.out.println(c.toString());
        }
    }

    /**
     * Metoda ce cauta toate cartile ce au un anumit status
     * @param carti {@code ArrayList<Carte>}; o lista cu cartile din biblioteca
     * @param status Status; statusul care se cauta
     * @return {@code ArrayList<Carte>}; o lista de carti care au statusul cerut
     */
    public static ArrayList<Carte> getBooksByStatus(ArrayList<Carte> carti, Status status) {
        ArrayList<Carte> carteS = new ArrayList<>();
        for(Carte c : carti) {
            if(c.getStatus().equals(status)) {
                carteS.add(c);
            }
        }
        return carteS;
    }

    /**
     * Metoda care schimba statusul unei carti pentru care se cunoaste id-ul si starea actuala
     * @param carti {@code ArrayList<Carte>}; lista cu toate cartile din biblioteca
     * @param idCarte int; id-ul cartii pentru care se schimba statusul
     * @param status1 Status; statusul actual al cartii, inainte de schimbare
     * @param status2 Status; noua stare a cartii - cea in care se va schimba
     */
    public static void changeStatus(ArrayList<Carte> carti, int idCarte, Status status1, Status status2) {
        for(Carte c : carti) {
            if(c.getId()==idCarte &&  c.getStatus().equals(status1)) {
                c.setStatus(status2);
                if(status2.equals(Status.IN_CURS_DE_CITIRE))
                    Logica_aplicatiei.startReading(c);
                System.out.println("Status schimbat in:" + c.getStatus());
                return;
            }
        }
    }

    /**
     * Metoda ce afiseaza id-ul, titlul si autorul unei carti
     * @param carte {@code ArrayList<Carte>}; lista de carti din biblioteca
     */
    public static void afisareTitluAutorId(ArrayList<Carte> carte) {
        for(Carte c : carte) {
            System.out.println(c.getId() + " " + c.getTitlu() + " " + c.getAutor());
        }
    }

    /**
     * Metoda ce filtreaza cartile dupa status - cu interfata in consola
     * @param carte {@code ArrayList<Carte>}; lista de carti din biblioteca
     * @param sc Scanner; scanner pentru citirea de la tastatura
     * @return {@code ArrayList<Carte>}; lista cu cartile filtrate in functie de optiunea selectata de utilizator
     */
    public static ArrayList<Carte> filtrareCarti(ArrayList<Carte> carte, Scanner sc) {
        ArrayList<Carte> carteS = new ArrayList<>();
        System.out.println("Optiune selectata: Filtrare dupa status");
        System.out.println("0 - Abandonata\n1 - In curs de citire\n2 - Finalizata\n3 - Necitita");
        System.out.println("Statusul dupa care se face sortarea:");
        int option = Integer.parseInt(sc.nextLine().trim());
        switch(option)
        {
            case 0: carteS=UtilitaraCarte.getBooksByStatus(carte, Status.ABANDONATA); break;
            case 1: carteS=UtilitaraCarte.getBooksByStatus(carte, Status.IN_CURS_DE_CITIRE); break;
            case 2: carteS=UtilitaraCarte.getBooksByStatus(carte, Status.FINALIZATA); break;
            case 3: carteS=UtilitaraCarte.getBooksByStatus(carte, Status.NECITITA); break;
            default: System.out.println("Optiune selectata invalida");
        }
        return carteS;
    }

    /**
     * Metoda pentru stergerea unei carti din biblioteca
     * @param carte {@code ArrayList<Carte>}; lista de carti din biblioteca
     * @param sc Scanner; scanner pentru citirea de la tastatura
     */
    public static void deleteBook(ArrayList<Carte> carte, Scanner sc) {
        try{
            System.out.println("Optiune selectata: stergere carte. Id carte:");
            int idC = Integer.parseInt(sc.nextLine().trim());
            boolean gasit=false;
            for(Carte c : carte) {
                if (idC == c.getId()) {
                    carte.remove(c);
                    System.out.println("Carte gasita si stearsa!");
                    return;
                }
            }
            if(!gasit) System.out.println("Cartea nu a fost gasita!");
        }catch(Exception e){
            System.out.println("Eroare la stergerea cartii " + e.getMessage());

        }
    }

    /**
     * Metoda prin care se adauga o noua carte in biblioteca
     * @param carti {@code ArrayList<Carte>}; lista de carti din biblioteca
     * @param sc Scannerl scanner pentru citirea de la tastatura
     */
    public static void adaugareCarte(ArrayList<Carte> carti, Scanner sc) {
        try{
            System.out.println("Optiune selectata: adaugare carte.");

            int idC=1;
            if(!carti.isEmpty())  idC =  carti.get(carti.size() - 1).getId() + 1;

            String titlu = Logica_aplicatiei.campObliogatoriu(sc, "Titlu:");
            String autor = Logica_aplicatiei.campObliogatoriu(sc, "Autor:");
            String gen   = Logica_aplicatiei.campObliogatoriu(sc, "Gen:");

            Format tip=null;
            System.out.println("Format:\n 1 - Ebook\n 2 - Paperback\n 3 - Hardcoveer");
            int option = Integer.parseInt(sc.nextLine().trim());
            switch(option){
                case 1: tip=Format.EBOOK;break;
                case 2: tip=Format.PAPERBACK;break;
                case 3: tip=Format.HARDCOVER;break;
                default: System.out.println("Optiune selectata invalida");
            }

            System.out.print("Imagine (optional, Enter pentru gol): ");
            String imagine = sc.nextLine().trim();

            int nrPgTot = Integer.parseInt(Logica_aplicatiei.campObliogatoriu(sc,"Numarul total de pagini (intreg): ").trim());

            String status = Logica_aplicatiei.campObliogatoriu(sc, "Status (NECITITA / IN_CURS_DE_CITIRE / FINALIZATA / ABANDONATA):");

            double procent = 0.0;
            LocalDate dataStart = null;
            LocalDate dataFinish = null;
            double scor = 0.0;
            String review = "";
            int paginaCurenta = 0;
            Status st;

            switch (status) {
                case "NECITITA":
                    st=Status.NECITITA;
                    break;
                case "IN_CURS_DE_CITIRE" :
                    dataStart = LocalDate.parse(Logica_aplicatiei.campObliogatoriu(sc, "Data start (YYYY-MM-DD):"));
                    paginaCurenta = Integer.parseInt( Logica_aplicatiei.campObliogatoriu(sc, "Pagina curenta:"));
                    procent = paginaCurenta / (double) nrPgTot;
                    st=Status.IN_CURS_DE_CITIRE;
                    break;
                case "ABANDONATA":
                    dataStart = LocalDate.parse(Logica_aplicatiei.campObliogatoriu(sc, "Data start (YYYY-MM-DD):"));
                    paginaCurenta = Integer.parseInt( Logica_aplicatiei.campObliogatoriu(sc, "Pagina curenta:"));
                    procent = paginaCurenta / (double) nrPgTot;
                    st=Status.ABANDONATA;
                    break;
                case "FINALIZATA":
                    dataStart = LocalDate.parse(Logica_aplicatiei.campObliogatoriu(sc, "Data start (YYYY-MM-DD):"));
                    dataFinish = LocalDate.parse(Logica_aplicatiei.campObliogatoriu(sc, "Data start (YYYY-MM-DD):"));
                    procent = 1.0;
                    st=Status.FINALIZATA;
                    scor = Double.parseDouble( Logica_aplicatiei.campObliogatoriu(sc, "Pagina curenta:"));
                    System.out.print("Review (optional, Enter pentru gol): ");
                    review = sc.nextLine();
                    break;

                default:
                    System.out.println("Status necunoscut.");
                    return;
            }

            Carte c = new Carte(idC, titlu, autor, gen, tip, nrPgTot, imagine, st, procent, dataStart, dataFinish, scor, review);

            carti.add(c);
            System.out.println("Carte adaugata:");
            System.out.println(c);

        }catch(Exception e){
            System.out.println("Eroare la adqaugare carte " + e.getMessage());

        }
    }
}
