package Logica;

import Modele_de_date.Jurnal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtilitaraJurnal {

    @Test
    @DisplayName("Test 1 - afisare jurnal")
    public void test1(){
        ArrayList<Jurnal> jurnal = new ArrayList<>();
        jurnal.add(new Jurnal(1, 101, LocalDate.of(2025, 1, 1), 50));
        jurnal.add(new Jurnal(2, 102, LocalDate.of(2025, 2, 10), 30));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        UtilitaraJurnal.afisare(jurnal);
        System.setOut(originalOut);
        String output = out.toString();

        assertAll(
                () -> assertTrue(output.contains("101")),
                () -> assertTrue(output.contains("102")),
                () -> assertTrue(output.contains("50")),
                () -> assertTrue(output.contains("30")),
                () -> assertTrue(output.contains("2025-01-01")),
                () -> assertTrue(output.contains("2025-02-10"))
        );
    }

    @Test
    @DisplayName("Test 2 - afisare jurnal")
    public void test2(){
        ArrayList<Jurnal> jurnal = new ArrayList<>();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        assertDoesNotThrow(() -> UtilitaraJurnal.afisare(jurnal));
        System.setOut(originalOut);
        String output = out.toString();
        assertTrue(output.isEmpty());
    }
}
