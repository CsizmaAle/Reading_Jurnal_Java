package UI;

import Modele_de_date.Carte;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;
import static Helpers.HelperBookCard.createCoverNode;

/**
 * Clasa ce este responsabila de interfata grafica pentru pagina cu detaliile referitoare la o carte
 */
public class BookDetailsPage extends VBox {

    /**
     * Metoda ce este responssabila de crearea interfetei grafice pentru pagina cu detaliile unei carti
     * @param book Carte; cartea pentru care se afiseaza detaliile
     * @param onBack Runnable; callback pentru intoarcere la ecranul anterior
     * @param onEdit {@Consumer<Carte>} - callback care primeste o carte si  nu returneaza nimic
     * @param onAddProgress {@Consumer<Carte>} - callback care primeste o carte si  nu returneaza nimic
     */
    public BookDetailsPage(Carte book, Runnable onBack, Consumer<Carte> onEdit, Consumer<Carte> onAddProgress) {
        setSpacing(20);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(book.getTitlu());
        title.getStyleClass().add("card-title");
        header.getChildren().add(title);

        HBox top = new HBox(24);
        top.setAlignment(Pos.TOP_LEFT);
        top.getChildren().addAll(createCoverNode(book.getImagine()), createDetailsGrid(book));

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button editInfo = new Button("Edit info");
        Button addProgress = new Button("Add progress");
        styleActionButton(editInfo);
        styleActionButton(addProgress);
        actions.getChildren().addAll(editInfo, addProgress);
        editInfo.setOnAction(e -> { if (onEdit != null) onEdit.accept(book); });
        addProgress.setOnAction(e -> { if (onAddProgress != null) onAddProgress.accept(book);});

        Button back = new Button("Back");
        back.setOnAction(e -> onBack.run());

        HBox footer = new HBox(back);
        footer.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(header, top, actions, spacer, footer);
    }

    /**
     * Metoda ce creeaza gridul pentru detaliile cartii
     * @param book Carte; cartea pentru care se afiseaza informatiile
     * @return Node; returneaza gridul creat cu informatiile despre carte
     */
    private Node createDetailsGrid(Carte book) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);

        addRow(grid, 0, "Author", book.getAutor());
        addRow(grid, 1, "Status", String.valueOf(book.getStatus()));
        addRow(grid, 2, "Pages", String.valueOf(book.getNumar_pagini_totale()));
        addRow(grid, 3, "Progress", String.valueOf(book.getProcent()));
        addRow(grid, 4, "Start date", book.getDataStart() == null ? "-" : book.getDataStart().toString());
        addRow(grid, 5, "Finish date", book.getDataFinish() == null ? "-" : book.getDataFinish().toString());
        addRow(grid, 6, "Score", String.valueOf(book.getScor()));
        addRow(grid, 7, "Review", book.getReview() == null || book.getReview().isBlank() ? "-" : book.getReview());

        return grid;
    }

    /**
     * Metoda ce este responsabila de adaugarea randurilor in grid-ul in care se afiseaza informatiile legate de carte
     * @param grid GridPane; gridul in care se adauga randul
     * @param row int; numarul randului adaugat
     * @param label String; numele randului
     * @param value String; informatia care se afiseaza
     */
    private void addRow(GridPane grid, int row, String label, String value) {
        Label l = new Label(label + ":");
        l.getStyleClass().add("subtitle");
        Label v = new Label(value == null || value.isBlank() ? "-" : value);
        v.setWrapText(true);
        v.setStyle("-fx-font-size: 15px;");
        grid.addRow(row, l, v);
    }

    /**
     * Metoda folosita pentru a seta stilul unui buton
     * @param btn Button; butonul pentru care se seteaza stilul
     */
    private void styleActionButton(Button btn) {
        btn.setStyle("""
                -fx-background-color: transparent;
                -fx-border-color: #bbbbbb;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """);
    }
}
