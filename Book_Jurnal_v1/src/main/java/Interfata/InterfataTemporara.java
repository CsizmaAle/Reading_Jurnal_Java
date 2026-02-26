package Interfata;

import Logica.Backup;
import Logica.Logica_aplicatiei;
import Logica.Statistici;
import Logica.UtilitaraCarte;
import Modele_de_date.Carte;
import Modele_de_date.Jurnal;
import Modele_de_date.Status;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Clasa temporara pentru interfetele proiectului -  rol demonstrativ
 */
public class InterfataTemporara {

    /**
     * Metoda ce simuleaza o interfata in consola pentru aplicatie - bara de navigare
     */
    public static void interfataMain(ArrayList<Jurnal> jurnal, ArrayList<Carte> carte) {

        try{
            int option;
            Scanner sc = new Scanner(System.in);
            do{
                System.out.println("0.Exit");
                System.out.println("1.Ecranul Home");
                System.out.println("2.Ecranul Statistici");
                System.out.println("3.Ecranul Biblioteca");
                System.out.println("\nIntroduceti optiunea dorita: ");
                option = Integer.parseInt(sc.nextLine().trim());

                switch(option){
                    case 0: break;
                    case 1: interfataHome(carte,jurnal, sc); break;
                    case 2: interfataStatistici(carte,jurnal, sc); break;
                    case 3: interfataBiblioteca(carte,jurnal, sc); break;
                    default: System.out.println("Optiune invalida"); break;
                }

            }while(option!=0);
        }catch(Exception e){
            System.out.println("EROARE: " + e.getMessage());
        }

    }

    /**
     * Metoda ce simuleaza o interfata in consola pentru aplicatie - ecranul Biblioteca
     */
    private static void interfataBiblioteca(ArrayList<Carte> carte, ArrayList<Jurnal> jurnal, Scanner sc) {
        System.out.println("Optiunea pentru ecranul Biblioteca selectata");
        UtilitaraCarte.afisareTitluAutorId(carte);

        try{
            int option;

            do {
                System.out.println("Optiunile pentru acest ecran:");
                System.out.println("\t0.Inapoi la bara de navigare");
                System.out.println("\t1.Filtrare carti in functie de status");
                System.out.println("\t2.Detalii carte");
                System.out.println("\t3.Editare carte");
                System.out.println("\t4.Adaugare carte");
                System.out.println("\t5.Stergere carte");
                System.out.println("Optiune selectata: ");
                option = Integer.parseInt(sc.nextLine().trim());

                switch(option){
                    case 0: System.out.println("Intoarcere la bara de nevigare");break;
                    case 1: ArrayList<Carte> cartiF= UtilitaraCarte.filtrareCarti(carte, sc);
                        System.out.println("Cartile filtrate sunt:");
                        UtilitaraCarte.afisareTitluAutorId(cartiF);
                        break;
                    case 2:Logica_aplicatiei.detaliiCarte(carte,sc); break;
                    case 3: Logica_aplicatiei.editareCarte(carte,sc); break;
                    case 4: UtilitaraCarte.adaugareCarte(carte,sc); break;
                    case 5:UtilitaraCarte.deleteBook(carte,sc); break;
                    default: System.out.println("Optiune invalida");
                }

            }while(option!=0);
        }catch(Exception e){
            System.out.println("EROARE: " + e.getMessage());
        }

    }

    /**
     * Metoda ce simuleaza o interfata in consola pentru aplicatie - ecranul Statistici
     */
    private static void interfataStatistici(ArrayList<Carte> carte, ArrayList<Jurnal> jurnal, Scanner sc) {
        System.out.println("Optiune selectata: ecranul Statistici ");
        int nrCarti=Statistici.numarTotalCarti(carte);
        int nrCartiCitite=Statistici.numarCartiCitite(carte);
        double pr=(nrCartiCitite/ (double) nrCarti)*100;
        String s = String.format("%.2f", pr);
        System.out.println("Numarul de carti din biblioteca: " + nrCarti);
        System.out.println("Numarul de carti citite din biblioteca: " + nrCartiCitite);
        System.out.println("Procentul de carti citite din biblioteca: " + s + "%");
    }

    /**
     * Metoda ce simuleaza o interfata in consola pentru aplicatie - ecranul Home
     */
    private static void interfataHome(ArrayList<Carte> carti, ArrayList<Jurnal> jurnal, Scanner sc) {
        System.out.println("Optiunea pentru ecranul Home selectata");

        System.out.println("Cartile care sunt in curs de citire sunt:");
        ArrayList<Carte> carteS = UtilitaraCarte.getBooksByStatus(carti,Status.IN_CURS_DE_CITIRE);
        UtilitaraCarte.afisare(carteS);

        System.out.println("Statisticile pentru luna curenta sunt:");
        //Statistici - in lucru

        int option;

        do {
            System.out.println("Optiunile pentru acest ecran:");
            System.out.println("\t0.Inapoi la bara de navigare");
            System.out.println("\t1.Inregistrare progres");
            System.out.println("\t2.Abandonare carte");
            System.out.println("\t3.Deschide statistici");
            System.out.println("\t3.Backup-export");
            System.out.println("Optiune selectata: ");
            option = Integer.parseInt(sc.nextLine().trim());

            switch(option){
                case 0: System.out.println("Intoarcere la bara de nevigare");break;
                case 1:
                    System.out.println("Optiune selectata: Inregistrare progres. Id carte:");
                    try{
                        int idCarte = Integer.parseInt(sc.nextLine().trim());
                        Logica_aplicatiei.readingProgress(carti, jurnal, idCarte, sc);
                    }catch(Exception e){
                        System.out.println("Optiune invalida");
                    }

                    break;
                case 2:
                    System.out.println("Optiune selectata: Abandonare carte. Id carte:");
                    try{
                        int idCarte = Integer.parseInt(sc.nextLine().trim());
                        UtilitaraCarte.changeStatus(carti, idCarte,Status.IN_CURS_DE_CITIRE ,Status.ABANDONATA);
                    }catch(Exception e){
                        System.out.println("Optiune invalida");
                    }

                case 3: interfataStatistici(carteS, jurnal, sc); break;
                case 4:
                    Backup.exportCSV("data\\cartiExport.csv",carti); break;
                default: System.out.println("Optiune invalida");
            }

        }while(option!=0);

    }
}
