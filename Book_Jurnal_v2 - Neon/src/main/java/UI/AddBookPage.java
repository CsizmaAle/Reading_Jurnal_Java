package UI;

import Helpers.HelperAddEditBook;
import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Status;
import Services.BookEditService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

import static Helpers.HelperAddEditBook.addRow;
import static Helpers.HelperAddEditBook.stringOrEmpty;

/**
 * Clasa ce estre responsabila de interfata grafica pentru pagina de adaugare carti
 */
public class AddBookPage extends VBox {
    /**
     * Metoda ce are ca rol interfata grafica pentru adaugarea unei carti in baza de date
     * @param book Carte; cartea ce urmeaza a fi adaugata
     * @param onCancel Runnable; callback - folosit cand utilizatorul doreste sa dea cancel operatiei si  se intoarce la pagina anterioara
     * @param onSaved {@code Consummer<Carte>} - callback care primeste o carte si nu returneaza nimic - folosit ca sa se trimita cartea catre logica de addBook()
     */
    public AddBookPage(Carte book, Runnable onCancel, Consumer<Carte> onSaved) {
        setSpacing(16);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        Label title = new Label("Add book");
        title.getStyleClass().add("card-title");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);

        TextField titlu = new TextField(stringOrEmpty(book.getTitlu()));
        TextField autor = new TextField(stringOrEmpty(book.getAutor()));
        TextField gen = new TextField(stringOrEmpty(book.getGen()));
        ComboBox<Format> tip = new ComboBox<>();
        tip.getItems().setAll(Format.values());
        tip.setValue(book.getTip() == null ? Format.PAPERBACK : book.getTip());

        TextField paginiTotale = new TextField(String.valueOf(book.getNumar_pagini_totale()));
        ComboBox<Status> status = new ComboBox<>();
        status.getItems().setAll(Status.values());
        status.setValue(book.getStatus() == null ? Status.NECITITA : book.getStatus());

        TextField imagine = new TextField(stringOrEmpty(book.getImagine()));
        TextField procent = new TextField(String.valueOf(book.getProcent()));
        DatePicker dataStart = new DatePicker(book.getDataStart());
        DatePicker dataFinish = new DatePicker(book.getDataFinish());
        TextField scor = new TextField(String.valueOf(book.getScor()));

        TextArea review = new TextArea(stringOrEmpty(book.getReview()));
        review.setWrapText(true);
        review.setPrefRowCount(4);

        int row = 0;
        addRow(form, row++, "Title", titlu);
        addRow(form, row++, "Author", autor);
        addRow(form, row++, "Genre", gen);
        addRow(form, row++, "Format", tip);
        addRow(form, row++, "Total pages", paginiTotale);
        addRow(form, row++, "Status", status);
        addRow(form, row++, "Image URL", imagine);
        addRow(form, row++, "Progress (0-1)", procent);
        addRow(form, row++, "Start date", dataStart);
        addRow(form, row++, "Finish date", dataFinish);
        addRow(form, row++, "Score (1-5)", scor);
        addRow(form, row++, "Review", review);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button save = new Button("Save");
        Button cancel = new Button("Cancel");
        actions.getChildren().addAll(save, cancel);

        save.setOnAction(e -> {
            try {
                HelperAddEditBook.applyToBook(book, titlu, autor, gen, tip, paginiTotale, status, imagine, procent, dataStart, dataFinish, scor, review);
                BookEditService.addBook(book);
                if (onSaved != null) onSaved.accept(book);
            } catch (Exception ex) {
                HelperAddEditBook.showError(ex.getMessage(), "Adaugare carte");
            }
        });

        cancel.setOnAction(e -> onCancel.run());
        getChildren().addAll(title, form, actions);
    }



}
