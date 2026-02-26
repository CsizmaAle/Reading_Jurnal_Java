package UI;

import Modele_de_date.Carte;
import Modele_de_date.Format;
import Modele_de_date.Status;
import Services.BookEditService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;
import static Helpers.HelperAddEditBook.*;

/**
 * Clasa care are ca rol interfata grafica pentru PAgina de ediatre a informatiilor legate de carti
 */
public class BookEditInfo extends VBox {
    /**
     * Constructor pentru pagina de editare a informatiilor
     * @param book Carte; cartea pentru care se face editarea
     * @param onCancel Runnable; callback pentru intoarcere la pagin antertioara
     * @param onSaved {@code Consumer<Carte>} - callback pentru transmiterea informatiilor legate de carte cand se da save
     */
    public BookEditInfo(Carte book, Runnable onCancel, Consumer<Carte> onSaved) {
        setSpacing(16);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        Label title = new Label("Edit book");
        title.getStyleClass().add("card-title");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);

        TextField titlu = new TextField(stringOrEmpty(book.getTitlu()));
        TextField autor = new TextField(stringOrEmpty(book.getAutor()));
        TextField gen = new TextField(stringOrEmpty(book.getGen()));
        ComboBox<Format> tip = new ComboBox<>();
        tip.getItems().setAll(Format.values());
        tip.setValue(book.getTip());

        TextField paginiTotale = new TextField(String.valueOf(book.getNumar_pagini_totale()));
        ComboBox<Status> status = new ComboBox<>();
        status.getItems().setAll(Status.values());
        status.setValue(book.getStatus());

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
                applyToBook(book, titlu, autor, gen, tip, paginiTotale, status, imagine, procent, dataStart, dataFinish, scor, review);
                BookEditService.updateBook(book);
                if (onSaved != null) onSaved.accept(book);
            } catch (Exception ex) {
                showError(ex.getMessage(), "Book Edit Info");
            }
        });

        cancel.setOnAction(e -> onCancel.run());
        getChildren().addAll(title, form, actions);
    }

}
