package Helpers;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Status;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class HelperAddEditBook {

    /**
     * Metoda ce adauga un rand in GridPane
     * @param grid GridPane; gridul in care se va adauga randul
     * @param row int; numarul randului ce se adauga
     * @param label String; numele randului (ex: Autor:)
     * @param field javafx.scene.Node; campul ce urmeaza a fi adaugat
     */
    public static void addRow(GridPane grid, int row, String label, javafx.scene.Node field) {
        Label l = new Label(label + ":");
        l.setStyle("-fx-opacity: 0.7;");
        grid.addRow(row, l, field);
    }

    /**
     * Metoda ce preia datele introduse de utilizator, le verifica si le adauga in  obiectul Carte
     * @param book Carte; reprezinta un obiect de tip carte in care se adauga valorile introduse de utilizator
     * @param titlu TextField; Titlul cartii
     * @param autor TextField; Autorul cartii
     * @param gen TextField; Genul cartii
     * @param tip {@code ComboBox<Format>} un dorpbox ce contine formatul cartii
     * @param paginiTotale TextField; Numarul de pagini al cartii
     * @param status {@code ComboBox<Status>} un dorpbox ce contine statusul cartii
     * @param imagine TextField; Coperta cartii in formal URL al cartii
     * @param procent TextField; Procentul de pagini citite din carte cartii
     * @param dataStart DatePicker; data la care s-a inceput lectura
     * @param dataFinish DatePicker; data finalizarii cartii
     * @param scor TextField; scorul acordat cartii (0-5)
     * @param review TextArea; review-ul lasat de utilizator cartii
     */
    public static void applyToBook(Carte book, TextField titlu, TextField autor, TextField gen, ComboBox<Format> tip, TextField paginiTotale, ComboBox<Status> status, TextField imagine, TextField procent, DatePicker dataStart, DatePicker dataFinish, TextField scor, TextArea review) {
        String titluVal = titlu.getText().trim();
        String autorVal = autor.getText().trim();
        if (titluVal.isEmpty()) throw new IllegalArgumentException("Title is required");
        if (autorVal.isEmpty()) throw new IllegalArgumentException("Author is required");

        book.setTitlu(titluVal);
        book.setAutor(autorVal);
        book.setGen(gen.getText().trim());
        book.setTip(tip.getValue() == null ? Format.PAPERBACK : tip.getValue());

        int pages = parseInt(paginiTotale.getText().trim(), "Total pages");
        if (pages <= 0) throw new IllegalArgumentException("Total pages must be > 0");

        book.setNumar_pagini_totale(pages);
        book.setStatus(status.getValue() == null ? Status.NECITITA : status.getValue());
        book.setImagine(blankToNull(imagine.getText()));
        String progressText = procent.getText().trim();
        if (progressText.isEmpty()) {
            book.setProcent(0);
        } else {
            double progressVal = parseDouble(progressText, "Progress");
            if (progressVal < 0 || progressVal > 100) throw new IllegalArgumentException("Progress must be between 0 and 100");
            book.setProcent(progressVal);
        }

        LocalDate start = dataStart.getValue();
        LocalDate finish = dataFinish.getValue();
        book.setDataStart(start);
        book.setDataFinish(finish);

        String scorText = scor.getText().trim();
        if (scorText.isEmpty()) {
            book.setScor(0);
        } else {
            double score = parseDouble(scorText, "Score");
            book.setScor(score);
        }
        book.setReview(blankToNull(review.getText()));
    }

    /**
     * Metoda ce transforam textul introdus de utilizator in numar si verifica sa nu fie introdus altceva
     * @param text String; textul introdus de utilizator in camp destinat numerelor
     * @param label String; numele campului folosit in caz de eroare
     * @return int; numarul introdus de utilizator
     */
    public static int parseInt(String text, String label) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            throw new IllegalArgumentException(label + " must be a number");
        }
    }

    /**
     * Metoda ce transforam textul introdus de utilizator in numar si verifica sa nu fie introdus altceva
     * @param text String; textul introdus de utilizator in camp destinat numerelor
     * @param label String; numele campului folosit in caz de eroare
     * @return double; numarul introdus de utilizator
     */
    private static double parseDouble(String text, String label) {
        try {
            return Double.parseDouble(text.replace(",", "."));
        } catch (Exception e) {
            throw new IllegalArgumentException(label + " must be a number");
        }
    }

    /**
     * Metoda ce verifica daca s-a introdus text, daca nu returneaza null, altfel returneaza textul
     * @param value Srting; posibilul text introdus de utilizator
     * @return String; null - daca nu s-a introdus nimic si textul prelucrat daca s-a introdus text
     */
    public static String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Metoda folosita pentru a afisa un mesaj de eroare utilizatorului
     * @param msg String; mesajul pe care il va primi utilizatorul in caz de eroare
     * @param title String; titlul actiunii care e esuat
     */
    public static void showError(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText( title +" failed");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Metoda ce verifica daca s-a introdus text sau nu si returneaza " " sau text
     * @param value String; valoarea introdusa de utilizator in campurile de text
     * @return String; " " - daca nu s-a introdus nimic, String, daca s-a introdus text
     */
    public static String stringOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
