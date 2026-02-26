package Logica;

import Modele_de_date.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Clasa utilitara pentru backup - la jurnal prin JSON si la biblioteca prin csv
 */
public class Backup {

    /**
     * Metoda ce preia datele legate de carti dintr-un csv si returneaza o lista de carti
     * @param path String; locatia fisierului din care se preiau datele
     * @return {@code ArrayList<Carte>}; o lista ce contine cartile din biblioteca
     */
    public static ArrayList<Carte> importCSV(String path) {
        ArrayList<Carte> carti = new ArrayList<>();
        String linie;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();

            while ((linie = br.readLine()) != null)
            {
                String[] campuri = linie.split(";", -1);

                int id = Integer.parseInt(campuri[0]);
                String titlu = campuri[1];
                String autor = campuri[2];
                String gen = campuri[3];
                Format tip = Format.valueOf(campuri[4]);

                int nrPagini = 0;
                if(!campuri[5].isEmpty()) nrPagini = Integer.parseInt(campuri[5]);

                Status status = Status.valueOf(campuri[6]);
                String imagine = campuri[7];

                double procent = 0;
                if(!campuri[8].isEmpty()) procent= Double.parseDouble(campuri[8]);

                LocalDate dataStart =null;
                if(!campuri[9].isEmpty()) dataStart=LocalDate.parse(campuri[9]);

                LocalDate dataFinish=null;
                if(!campuri[10].isEmpty()) dataFinish=LocalDate.parse(campuri[10]);

                double scor = 0;
                if(!campuri[11].isEmpty())
                    scor=Double.parseDouble(campuri[11].replace(",", "."));
                String review = campuri[12];

                Carte carte = new Carte(id, titlu, autor, gen, tip, nrPagini, imagine,status, procent, dataStart,dataFinish, scor, review);
                carti.add(carte);
            }

        } catch (Exception e) {
            System.out.println("EROARE la import CSV: " + e.getMessage());
        }

        return carti;
    }

    /**
     * Metoda de export a informatiilor legate de biblioteca pesonala
     * @param path String; calea catre fisierul de export
     * @param carti {@code ArrayList<Carti>}; lista de carti din biblioteca
     */
    public static void exportCSV(String path, ArrayList<Carte> carti) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("id;titlu;autor;gen;tip;numar_pagini_totale;status;imagine;procent;dataStart;dataFinish;scor;review");
            String dataStart;
            String dataFinish;

            for (Carte c : carti)
            {
                dataStart="";
                dataFinish="";
                if (c.getDataStart()!=null) dataStart=c.getDataStart().toString();
                if (c.getDataFinish()!=null) dataFinish=c.getDataFinish().toString();

                pw.println(c.getId() + ";" + c.getTitlu() + ";" + c.getAutor() + ";" + c.getGen() + ";" + c.getTip() + ";" + c.getNumar_pagini_totale() +
                        ";" + c.getStatus() + ";" + c.getImagine() + ";" + c.getProcent() + ";" + dataStart + ";" + dataFinish + ";" + c.getScor() + ";" + c.getReview());
            }
            System.out.println("EXPORT CSV efectuat cu succes in: " + path);
        } catch (Exception e) {
            System.out.println("EROARE la export CSV: " + e.getMessage());
        }
    }

    /**
     * Metoda folosita pentru a importa datele de tip jurnal -  intrarile de jurnal din jormat JSON
     * @param path String; calea catre fisierul de import
     * @return {@code ArrayList<Jurnal>}; lista ce reprezinta intrarile in jurnal
     */
    public static ArrayList<Jurnal> importJSON(String path){
        ArrayList<Jurnal> jurnal = new ArrayList<>();
        String linie;
        try(BufferedReader br= new BufferedReader(new FileReader(path))){
            int id = 0;
            int idCarte = 0;
            int paginaCurenta = 0;
            LocalDate data=null;

            while((linie=br.readLine())!=null)
            {
                linie=linie.trim();
                if(linie.startsWith("\"id\":"))
                {
                    linie=linie.replace("\"id\":", "");
                    linie=linie.replace(",","");
                    linie=linie.trim();
                    id=Integer.parseInt(linie);
                }

                if(linie.startsWith("\"idCarte\":"))
                {
                    linie=linie.replace("\"idCarte\":", "");
                    linie=linie.replace(",","");
                    linie=linie.trim();
                    idCarte=Integer.parseInt(linie);
                }

                if(linie.startsWith("\"paginaCurenta\":"))
                {
                    linie=linie.replace("\"paginaCurenta\":", "");
                    linie=linie.replace(",","");
                    linie=linie.trim();
                    paginaCurenta=Integer.parseInt(linie);
                }

                if(linie.startsWith("\"data\":"))
                {
                    linie=linie.replace("\"data\":", "");
                    linie=linie.replace(",","");
                    linie=linie.trim();
                    if(!linie.isEmpty()) data=LocalDate.parse(linie.replace("\"",""));
                }

                if (linie.startsWith("}")) {
                    jurnal.add(new Jurnal(id, idCarte, data, paginaCurenta));
                    id=0;idCarte=0;paginaCurenta=0;
                    data=null;
                }
            }
        } catch (Exception e){
            System.out.println("EROARE la import JSON: " + e.getMessage());
        }

        return jurnal;
    }

    /**
     * Metoda folosita pentru a exporta datele de tip jurnal -  intrarile de jurnal in jormat JSON
     * @param path String; calea catre fisierul de export
     * @param jurnal {@code ArrayList<Jurnal>}; lista de intrari de jurnal
     */
    public static void exportJSON(String path, ArrayList<Jurnal> jurnal){
        try(PrintWriter pw = new PrintWriter(new FileWriter(path))){
            boolean primul=true;
            pw.println("[");
            for(Jurnal j : jurnal)
            {
                if(!primul) pw.println(",");
                primul=false;

                pw.println("\t{");
                pw.println("\t\"id\": " + j.getId() + ",");
                pw.println("\t\"idCarte\": " + j.getIdCarte() + ",");
                pw.println("\t\"data\": \"" + j.getData() + "\",");
                pw.println("\t\"paginaCurenta\": " + j.getPaginaCurenta() );
                pw.print("\t}");
            }
            pw.println("\n]");
            System.out.println("EXPORT JSON efectuat cu succes in: " + path);

        }catch(Exception e){
            System.out.println("EROARE la export JSON: " + e.getMessage());
        }
    }

}
