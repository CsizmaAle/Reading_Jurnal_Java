package UI;

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

public class HelperBookCard {

    public static Node createBookCard(Carte book, Consumer<Carte> onBookSelected) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(10));
        card.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #dddddd;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                """);

        Node cover = createCoverNode(book.getImagine());
        Label title = new Label(book.getTitlu());
        title.setWrapText(true);
        title.setMaxWidth(140);
        title.setStyle("-fx-font-weight: bold;");

        Label author = new Label(book.getAutor());
        author.setStyle("-fx-opacity: 0.7; -fx-font-size: 11px;");
        author.setWrapText(true);
        author.setMaxWidth(140);

        card.getChildren().addAll(cover, title, author);

        card.setOnMouseClicked(e -> {
            if (onBookSelected != null) {
                onBookSelected.accept(book);
            }
        });
        return card;
    }

    private static Node createCoverNode(String imagine) {
        double w = 140;
        double h = 190;

        Image image = null;
        String resolved = resolveImageUrl(imagine);
        if (resolved != null) {
            image = new Image(resolved, w, h, true, true, true);
        }

        if (image == null || image.isError()) {
            Rectangle rect = new Rectangle(w, h);
            rect.setArcWidth(8);
            rect.setArcHeight(8);
            rect.setFill(Color.web("#f0f0f0"));
            rect.setStroke(Color.web("#dddddd"));

            Label placeholder = new Label("No cover");
            placeholder.setStyle("-fx-opacity: 0.6; -fx-font-size: 11px;");

            StackPane pane = new StackPane(rect, placeholder);
            return pane;
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

    private static String resolveImageUrl(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("file:")) {
            return trimmed;
        }
        File f = new File(trimmed);
        if (f.exists()) {
            return f.toURI().toString();
        }
        return trimmed;
    }


}
