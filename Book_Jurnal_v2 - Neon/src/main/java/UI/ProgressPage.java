package UI;

import Baza_de_date.BDConnectionManager;
import Helpers.HelperAddEditBook;
import Logica.LogicaAplicatiei;
import Modele_de_date.Carte;
import Services.BookEditService;
import Services.UpdateProgressService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.util.function.Consumer;
import static Helpers.HelperAddEditBook.blankToNull;
import static Helpers.HelperAddEditBook.showError;

/**
 * Clasa ce are ca rol implementarea si gestionarea interfatei grafice - pagina pentru inregistrarea progresului
 */
public class ProgressPage extends VBox {
    /**
     * Constructor pentru implementarea si gestionarea interfatei grafice - pagina pentru inregistrarea progresului
     * @param book Carte; cartea pentru care se inregistreaza progresul
     * @param onCancel Runnable; callback pentru intoarcerea la pagina anterioara
     * @param onSaved {@code Consumer<Carte>}; folosit pentru transmiterea informatiilor legate de carte la logica de inregistrare a progresului
     */
    public ProgressPage(Carte book, Runnable onCancel, Consumer<Carte> onSaved) {
        setSpacing(16);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        Label title = new Label("Add progress");
        title.getStyleClass().add("card-title");

        Label hint = new Label("Current page (max " + book.getNumar_pagini_totale() + "):");
        TextField paginaCurenta = new TextField();
        paginaCurenta.setPromptText("e.g. 120");

        Label scoreLabel = new Label("Score (optional):");
        TextField score = new TextField();
        score.setPromptText("1-10");

        Label reviewLabel = new Label("Review (optional):");
        TextArea review = new TextArea();
        review.setWrapText(true);
        review.setPrefRowCount(3);

        HBox statusActions = new HBox(10);
        statusActions.setAlignment(Pos.CENTER_LEFT);
        Button startReading = new Button("Start reading");
        Button finishReading = new Button("Finish reading");
        Button abandon = new Button("GiveUp");
        statusActions.getChildren().addAll(startReading, finishReading, abandon);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button save = new Button("Save progress");
        Button cancel = new Button("Cancel");
        actions.getChildren().addAll(save, cancel);

        save.setOnAction(e -> {
            try {
                int page = HelperAddEditBook.parseInt(paginaCurenta.getText().trim(), "Pages");
                if (page < 0 || page > book.getNumar_pagini_totale()) throw new IllegalArgumentException("Page must be between 0 and total pages");
                try (Connection conn = BDConnectionManager.getConnection()) {
                    UpdateProgressService.readingProgress(book, page, conn);
                }
                if (onSaved != null) onSaved.accept(book);
            } catch (Exception ex) {
                showError(ex.getMessage(), "Save progress");
            }
        });

        startReading.setOnAction(e -> {
            try {
                LogicaAplicatiei.startReading(book);
                BookEditService.updateBook(book);
                if (onSaved != null) onSaved.accept(book);
            } catch (Exception ex) {
                showError(ex.getMessage(), "Start reading");
            }
        });

        finishReading.setOnAction(e -> {
            try {
                Double scoreValue = parseOptionalDouble(score.getText().trim());
                String reviewValue = blankToNull(review.getText());
                LogicaAplicatiei.finishReading(book, scoreValue, reviewValue);
                BookEditService.updateBook(book);
                if (onSaved != null) onSaved.accept(book);
            } catch (Exception ex) {
                showError(ex.getMessage(), "Finish reading");
            }
        });

        abandon.setOnAction(e -> {
            try {
                LogicaAplicatiei.abandonReading(book);
                BookEditService.updateBook(book);
                if (onSaved != null) onSaved.accept(book);
            } catch (Exception ex) {
                showError(ex.getMessage(), "Give up book");
            }
        });

        cancel.setOnAction(e -> onCancel.run());
        getChildren().addAll(title, hint, paginaCurenta, scoreLabel, score, reviewLabel, review, statusActions, actions);
    }

    /**
     * Metoda care transforma text in double (camp optional)
     * @param text String; textul ce reprezinta un numar
     * @return null- daca nu s-a introdus nimic, double - numarul transformat din string
     */
    private Double parseOptionalDouble(String text) {
        if (text == null || text.isBlank()) return null;

        try {
            return Double.parseDouble(text.replace(",", "."));
        } catch (Exception e) {
            throw new IllegalArgumentException("Score must be a number");
        }
    }

}
