package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Jurnal;
import Modele_de_date.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Clasa utilitara ce contine metode ce definesc diverse functii importante ale aplicatiei
 */
public class Logica_aplicatiei {

    /**
     * Metoda care inregistreaza progresul cititorului
     * @param carti {@code ArrayList<Carte>}; lista de carti din biblioteca - se va modifica
     * @param jurnal {@code ArrayList<Jurnal>}; lista de intrari in jurnalul de citire - se va modifica
     * @param idCarte int; id-ul pentru cartea pentru care se doreste a fi inregistrat progresul
     * @param sc Scanner; pentru citirea de la tastatura
     */
    public static void readingProgress(ArrayList<Carte> carti, ArrayList<Jurnal> jurnal, int idCarte, Scanner sc) {
        try {
            int ok=0;
            for (Carte c : carti) {
                if (idCarte == c.getId() && c.getStatus().equals(Status.IN_CURS_DE_CITIRE))
                {
                    ok=1;
                    System.out.println("Pagina curenta:");
                    int pg = Integer.parseInt(sc.nextLine().trim());
                    if(pg>0 && pg<c.getNumar_pagini_totale())
                    {
                        if(jurnal.isEmpty())
                            jurnal.add(new Jurnal( 1, idCarte, LocalDate.now(), pg));
                         else
                            jurnal.add(new Jurnal(jurnal.get(jurnal.size() - 1).getId() + 1, idCarte, LocalDate.now(), pg));
                        c.setProcent((double) pg/ c.getNumar_pagini_totale());
                    }
                    else if(pg==c.getNumar_pagini_totale())
                    {
                        jurnal.add(new Jurnal(jurnal.get(jurnal.size() - 1).getId() + 1, idCarte, LocalDate.now(), pg));
                        Logica_aplicatiei.finishReading(c,sc);
                    }
                    break;
                }
            }
            if(ok==0)
                System.out.println("Cartea nu a fost gasita!");
        } catch (Exception ex) {
            System.out.println("Eroare la inregistrarea progresului. " + ex.getMessage());
        }

    }

    /**
     * Metoda ce schimba informatii legate de carte dupa terminare ei: scor, status, procent,dataFinish, scor, review
     * @param c Carte; cartea care a fost terminata de citit
     * @param sc Scanner; folosit pentru a citi de la tastatura informatii
     */
    private static void finishReading(Carte c, Scanner sc) {
        c.setProcent(1);
        c.setDataFinish(LocalDate.now());
        c.setStatus(Status.FINALIZATA);
        System.out.println("Scor din 5:");
        double score = Double.parseDouble(sc.nextLine().trim());
        c.setScor(score);
        System.out.println("Review (optional):");
        String review = sc.nextLine();
        c.setReview(review);
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
     * Metoda care afiseaza detaliile pentru o carte aleasa de utilizator
     * @param carte {@code ArrayList<Carte>}; lista de carti din biblioteca
     * @param sc Scanner; scanner pentru a citi ce introduce utilizatrorul de la tastatura
     */
    public static void detaliiCarte(ArrayList<Carte> carte, Scanner sc) {
        try{
            System.out.println("Optiune selectata: detalii carte. Id carte:");
            int idC = Integer.parseInt(sc.nextLine().trim());
            boolean gasit=false;
            for(Carte c : carte) {
                if (idC == c.getId()) {
                    System.out.println(c.toString());
                    return;
                }
            }
            if(!gasit) System.out.println("Cartea nu a fost gasita!");
        }catch(Exception e){
            System.out.println("Eroare la afisare detalii carte. " + e.getMessage());

        }
    }

    /**
     * Metoda pentru editarea informatiilor unei carti de catre utilizator
     * @param carte {@code ArrayList<Carte>}; lista de carti din biblioteca
     * @param sc Scanner; scanner pentru citirea informatiilor de al tastatura
     */
    public static void editareCarte(ArrayList<Carte> carte, Scanner sc) {
        try{
            System.out.println("Optiune selectata: editare carte. Id carte:");
            int idC = Integer.parseInt(sc.nextLine().trim());
            boolean gasit=false;
            for(Carte c : carte) {
                if (idC == c.getId()) {
                    System.out.println("Carte gasita!");
                    System.out.println(c.toString());
                    int opt;
                    do {
                        System.out.println("\tEDITARE CARTE");
                        System.out.println("1. Titlu");
                        System.out.println("2. Autor");
                        System.out.println("3. Gen");
                        System.out.println("4. Nr pagini");
                        System.out.println("5. Status");
                        System.out.println("6. Scor");
                        System.out.println("7. Review");
                        System.out.println("0. Gata");
                        System.out.println("Alege optiunea:");

                        opt = Integer.parseInt(sc.nextLine().trim());

                        switch (opt) {
                            case 1:
                                System.out.print("Titlu nou: ");
                                c.setTitlu(sc.nextLine());
                                break;
                            case 2:
                                System.out.print("Autor nou: ");
                                c.setAutor(sc.nextLine());
                                break;
                            case 3:
                                System.out.print("Gen nou: ");
                                c.setGen(sc.nextLine());
                                break;
                            case 4:
                                System.out.print("Nr pagini: ");
                                c.setNumar_pagini_totale(Integer.parseInt(sc.nextLine()));
                                break;
                            case 5:
                                System.out.print("Status (IN_CURS_DE_CITIRE / FINALIZATA): ");
                                c.setStatus(Status.valueOf(sc.nextLine().trim()));
                                break;
                            case 6:
                                System.out.print("Scor (0–5): ");
                                c.setScor(Double.parseDouble(sc.nextLine().replace(",", ".")));
                                break;
                            case 7:
                                System.out.print("Review: ");
                                c.setReview(sc.nextLine());
                                break;
                            case 0: System.out.println("Editare finalizata."); break;
                            default: System.out.println("Optiune invalida.");
                        }

                    } while (opt != 0);

                    System.out.println("Carte actualizata:");
                    System.out.println(c);
                    return;
                }
            }
            if(!gasit) System.out.println("Cartea nu a fost gasita!");
        }catch(Exception e){
            System.out.println("Eroare la editarea cartii " + e.getMessage());

        }
    }

    /**
     * Metoda care se asigura ca campurile obligatorii sunt completate - helper pentru metoda UtilitaraCarte.adaugareCarte()
     * @param sc Scanner; scanner pentru datele introduse de utilizator de la tastatura
     * @param s String; numele campului care se cere
     * @return String; informatia introdusa de utilizator
     */
    public static String campObliogatoriu(Scanner sc, String s) {
        while (true) {
            System.out.println(s);
            String st = sc.nextLine().trim();
            if (!st.isEmpty()) return st;
            System.out.println("Camp obligatoriu. Incearca din nou.");
        }
    }
}
