package Helpers;

import Modele_de_date.Carte;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.File;
import java.util.function.Consumer;

/**
 * Helper pentru interfate grafice legate de afisarea cartilor
 */
public class HelperBookCard {

    /**
     * Metoda ce are ca rol cearea zonei de afisare a unei carti + functionalitate de Click
     * @param book Carte; cartea pentru care se creeaza card
     * @param onBookSelected {@Consumer<Carte>} - callback pentru trimiterea cartii la deschiderea de detalii
     * @return Node; card-ul creat pentru cartea trimisa ca parametru
     */
    public static Node createBookCard(Carte book, Consumer<Carte> onBookSelected) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("book-card");

        Node cover = createCoverNode(book.getImagine());
        Label title = new Label(book.getTitlu());
        title.setWrapText(true);
        title.setMaxWidth(140);
        title.getStyleClass().add("book-title");

        Label author = new Label(book.getAutor());
        author.getStyleClass().add("book-author");
        author.setWrapText(true);
        author.setMaxWidth(140);

        card.getChildren().addAll(cover, title, author);
        card.setOnMouseClicked(e -> {if (onBookSelected != null) onBookSelected.accept(book);});
        return card;
    }

    /**
     * Metoda responsabila pentru crearea si gestionarea afisarii imaginii de coperta a cartii
     * @param imagine String; url-ul pentru imaginea cartii
     * @return Node; fie un StackPane gol (in cazul in care nu functioneaza imaginea), fie un ImageView cu imaginea copertii
     */
    public static Node createCoverNode(String imagine) {
        double w = 200;
        double h = 280;
        Image image = null;
        String resolved = resolveImageUrl(imagine);
        if (resolved != null) {
            try {
                image = new Image(resolved, w, h, true, true, true);
            } catch (IllegalArgumentException ignored) {
                image = null;
            }
        }

        if (image == null || image.isError()) {
            Rectangle rect = new Rectangle(w, h);
            rect.setArcWidth(8);
            rect.setArcHeight(8);
            rect.setFill(Color.web("#f0f0f0"));
            rect.setStroke(Color.web("#dddddd"));
            Label placeholder = new Label("No cover");
            placeholder.setStyle("-fx-opacity: 0.6; -fx-font-size: 11px;");

            return new StackPane(rect, placeholder);
        }

        ImageView view = new ImageView(image);
        view.setFitWidth(w);
        view.setFitHeight(h);
        view.setPreserveRatio(true);
        view.setSmooth(true);

        Rectangle clip = new Rectangle(w, h);
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        view.setClip(clip);

        return view;
    }

    /**
     * Metoda ce are ca scop curatarea url-ului cartii
     * @param value String; URL-ul cartii
     * @return String, URL-ul "curatat" al cartii
     */
    public static String resolveImageUrl(String value) {
        if (value == null || value.isBlank()) return null;

        String trimmed = value.trim();
        if ("null".equalsIgnoreCase(trimmed)) return null;
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("file:"))  return trimmed;

        File f = new File(trimmed);
        if (f.exists())  return f.toURI().toString();

        return null;
    }

}
