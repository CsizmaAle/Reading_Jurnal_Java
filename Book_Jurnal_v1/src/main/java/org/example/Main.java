package org.example;

import Interfata.InterfataTemporara;
import Logica.Backup;
import Logica.UtilitaraCarte;
import Logica.UtilitaraJurnal;
import Modele_de_date.Carte;
import Modele_de_date.Jurnal;

import java.util.ArrayList;

public class Main {
    static void main() {
        ArrayList<Carte> carti=Backup.importCSV("data\\cartiExport.csv");
        ArrayList<Jurnal> jurnal=Backup.importJSON("data\\jurnalImport.json");

        InterfataTemporara.interfataMain(jurnal, carti);

        Backup.exportCSV("data\\cartiExport.csv", carti);
        Backup.exportJSON("data\\jurnalExport.json",jurnal);

    }
}
