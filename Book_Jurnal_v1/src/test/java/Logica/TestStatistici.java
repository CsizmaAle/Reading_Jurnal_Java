package Logica;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Jurnal;
import Modele_de_date.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TestStatistici {
    @Test
    @DisplayName("Test 1 - numar total de carti")
    public void test1(){
        ArrayList<Carte> carti=new ArrayList<>();
        Carte c = new Carte(10, "Titlu", "Autor", "Gen", Format.PAPERBACK, 100, "img", Status.IN_CURS_DE_CITIRE, 0.0, null, null, 0.0, "");
        carti.add(c);
        assertEquals(1,Statistici.numarTotalCarti(carti));
    }

    @Test
    @DisplayName("Test 2 - numar total de carti citite")
    public void test2(){
        ArrayList<Carte> carti=new ArrayList<>();
        Carte c = new Carte(10, "Titlu", "Autor", "Gen", Format.PAPERBACK, 100, "img", Status.IN_CURS_DE_CITIRE, 0.0, null, null, 0.0, "");
        carti.add(c);
        assertEquals(0,Statistici.numarCartiCitite(carti));
    }

    @Test
    @DisplayName("Test 3 - numar total de carti citite")
    public void test3(){
        ArrayList<Carte> carti=new ArrayList<>();
        Carte c = new Carte(10, "Titlu", "Autor", "Gen", Format.PAPERBACK, 100, "img", Status.FINALIZATA, 0.0, null, null, 0.0, "");
        carti.add(c);
        assertEquals(1,Statistici.numarCartiCitite(carti));
    }
}
