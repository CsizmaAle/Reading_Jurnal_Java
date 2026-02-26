package UI;

import Modele_de_date.Carte;
import Services.BookEditService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.List;
import static Helpers.HelperAddEditBook.showError;
import static Services.BookSelectService.fetchBooksFromDB;

/**
 * Clasa ce are ca rol implementarea interfatei grafice responsabile pentru pagina de stergere a unei carti din biblioteca
 */
public class DeleteBookPage extends VBox {
    private final VBox list = new VBox(8);
    private final Runnable onBack;
    private final Runnable onDeleted;

    /**
     * Constructor pentru clasa DeleteBookPage
     * @param onBack Runnable; callback pentru intoarceere la pagina anterioara
     * @param onDeleted Runnable; callback pentru stergerea cartilor
     */
    public DeleteBookPage(Runnable onBack, Runnable onDeleted) {
        this.onBack = onBack;
        this.onDeleted = onDeleted;

        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("card");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Delete book");
        title.getStyleClass().add("card-title");
        Button back = new Button("Back");
        back.setOnAction(e -> onBack.run());
        header.getChildren().addAll(back, title);

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        getChildren().addAll(header, scroll);

        loadBooks();
    }

    /**
     * Metoda ce incarca cartile in pagina
     */
    private void loadBooks() {
        list.getChildren().clear();
        List<Carte> books = fetchBooksFromDB();
        for (Carte book : books) {
            list.getChildren().add(createRow(book));
        }
    }

    /**
     * Metoda ce creeaza randurile pe pagina
     * @param book Carte; cartea pentru care se creeaza randul
     * @return HBox; returneaza randul creat
     */
    private HBox createRow(Carte book) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8));
        row.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #dddddd;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        Label title = new Label(book.getTitlu());
        title.setStyle("-fx-font-weight: bold;");
        Label author = new Label(book.getAutor());
        author.setStyle("-fx-opacity: 0.7;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button delete = new Button("Delete");
        delete.setOnAction(e -> {
            try {
                BookEditService.deleteBook(book.getId());
                loadBooks();
                if (onDeleted != null) onDeleted.run();
            } catch (Exception ex) {
                showError(ex.getMessage(), "Delete Book ");
            }
        });

        row.getChildren().addAll(title, author, spacer, delete);
        return row;
    }

}
