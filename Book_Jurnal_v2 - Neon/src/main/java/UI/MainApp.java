package UI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import Modele_de_date.Status;

/**
 * Clasa ce se ocupa de interfata grafica a programului
 */
public class MainApp extends Application {
    private final BorderPane root = new BorderPane();

    private final ScrollPaneWrapper homeView = createHomeView();
    private final ScrollPaneWrapper libraryView = createLibraryView();
    private final ScrollPaneWrapper statsView = createStatsView();

    private Button btnHome;
    private Button btnLibrary;
    private Button btnStats;

    /**
     * Metoda care are ca scop pornirea aplicatiei
     * @param stage Stage; fereastra principala a aplicatiei (Home)
     */
    @Override
    public void start(Stage stage) {
        root.setCenter(homeView);
        root.setBottom(createBottomNav());
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(MainApp.class.getResource("/ui.css").toExternalForm());
        stage.setTitle("Book Journal");
        stage.setScene(scene);
        stage.show();
        setActive(btnHome);
    }

    /**
     * Metoda ce creeaza bara de navigare din partea de jos a ecranului
     * @return HBox; bara de navigare din partea de jos a ecranului
     */
    private HBox createBottomNav() {
        btnHome = new Button("Home");
        btnLibrary = new Button("Biblioteca");
        btnStats = new Button("Statistici");

        btnHome.setMaxWidth(Double.MAX_VALUE);
        btnLibrary.setMaxWidth(Double.MAX_VALUE);
        btnStats.setMaxWidth(Double.MAX_VALUE);

        btnHome.setOnAction(e -> {
            root.setCenter(homeView);
            setActive(btnHome);
        });

        btnLibrary.setOnAction(e -> {
            root.setCenter(libraryView);
            setActive(btnLibrary);
        });

        btnStats.setOnAction(e -> {
            root.setCenter(statsView);
            setActive(btnStats);
        });

        HBox nav = new HBox(10, btnHome, btnLibrary, btnStats);
        nav.setPadding(new Insets(10));
        nav.setAlignment(Pos.CENTER);
        nav.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.web("#f2f2f2"), CornerRadii.EMPTY, Insets.EMPTY)));
        HBox.setHgrow(btnHome, Priority.ALWAYS);
        HBox.setHgrow(btnLibrary, Priority.ALWAYS);
        HBox.setHgrow(btnStats, Priority.ALWAYS);

        return nav;
    }

    /**
     * Metoda pentru a evidentia care buton/functionalitate este activa
     * @param active Button; butonul care este activ
     */
    private void setActive(Button active) {
        btnHome.setStyle("");
        btnLibrary.setStyle("");
        btnStats.setStyle("");
        active.setStyle("-fx-font-weight: bold; -fx-background-color: #d9eaff;");
    }

    /**
     * Metoda ce creeaza si gestioneaza pagina Home si toate sectiunile aferente ei
     * @return ScrollPaneWrapper; pagina de home
     */
    private ScrollPaneWrapper createHomeView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label title = new Label("Home");
        title.getStyleClass().add("page-title");
        StackPane readingNowCard = new StackPane();

        ReadingBooksPanel[] holder = new ReadingBooksPanel[1];
        holder[0] = new ReadingBooksPanel(book -> {
            ProgressPage progressPage = new ProgressPage(book, () -> {
                holder[0].refresh();
                readingNowCard.getChildren().setAll(holder[0]);
            }, savedBook -> {
                holder[0].refresh();
                readingNowCard.getChildren().setAll(holder[0]);
            });
            readingNowCard.getChildren().setAll(progressPage);
        });
        readingNowCard.getChildren().add(holder[0]);

        Region statsCard = new HomeStatisticsPanel(() -> {
            root.setCenter(statsView);
            setActive(btnStats);
        });

        Region backupCard = new BackupPanel();

        box.getChildren().addAll(title, readingNowCard, statsCard, backupCard);
        return new ScrollPaneWrapper(box);
    }

    /**
     * Metoda ce creeaza si gestioneaza pagina Biblioteca si toate elementele aferente ei
     * @return ScrollPaneWrapper; pagina de biblioteca
     */
    private static ScrollPaneWrapper createLibraryView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label title = new Label("Biblioteca");
        title.getStyleClass().add("page-title");
        Button deleteBtn = new Button("Delete book");
        Button addBtn = new Button("Add book");

        ComboBox<Status> statusFilter = new ComboBox<>();
        statusFilter.getItems().add(null);
        statusFilter.getItems().addAll(Status.values());
        statusFilter.setValue(null);
        statusFilter.setConverter(new StringConverter<Status>() {
            @Override
            public String toString(Status status) {
                return status == null ? "All statuses" : status.name();
            }

            @Override
            public Status fromString(String string) {
                return string == null || string.isBlank() ? null : Status.valueOf(string);
            }
        });

        HBox header = new HBox(12, title, statusFilter, new Region(), addBtn, deleteBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);

        StackPane content = new StackPane();

        BookListPanel[] holder = new BookListPanel[1];
        holder[0] = new BookListPanel(book -> {
            addBtn.setVisible(false);
            deleteBtn.setVisible(false);
            content.getChildren().setAll(buildDetailsPage(content, holder, book, addBtn, deleteBtn));
        });
        content.getChildren().add(holder[0]);

        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            holder[0].setFilterStatus(newVal);
            holder[0].refresh();
        });

        deleteBtn.setOnAction(e -> {
            addBtn.setVisible(false);
            deleteBtn.setVisible(false);
            DeleteBookPage deletePage = new DeleteBookPage(() -> {
                holder[0].refresh();
                addBtn.setVisible(true);
                deleteBtn.setVisible(true);
                content.getChildren().setAll(holder[0]);
            }, () -> {
                holder[0].refresh();
            });
            content.getChildren().setAll(deletePage);
        });

        addBtn.setOnAction(e -> {
            addBtn.setVisible(false);
            deleteBtn.setVisible(false);
            AddBookPage addPage = new AddBookPage(new Modele_de_date.Carte(), () -> {
                holder[0].refresh();
                addBtn.setVisible(true);
                deleteBtn.setVisible(true);
                content.getChildren().setAll(holder[0]);
            }, savedBook -> {
                holder[0].refresh();
                addBtn.setVisible(true);
                deleteBtn.setVisible(true);
                content.getChildren().setAll(holder[0]);
            });
            content.getChildren().setAll(addPage);
        });

        box.getChildren().addAll(header, content);
        VBox.setVgrow(content, Priority.ALWAYS);

        ScrollPaneWrapper wrapper = new ScrollPaneWrapper(box);
        wrapper.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        wrapper.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        wrapper.setFitToHeight(true);
        return wrapper;
    }

    /**
     * Metoda ce creeaza si gestioneaza pagina Detalii si toate sectiunile aferente ei
     * @param content StackPane; containerul in care se schimba ecranul
     * @param addBtn Button; buton de Add Book care e ascuns
     * @param book Carte; cartea pentru care se construieste pagina
     * @param holder BookListPanel[]; referinta la panelul cu lista de carti
     * @param deleteBtn Button; buton de Delete Book care e ascuns
     * @return ScrollPaneWrapper; pagina de detalii
     */
    private static BookDetailsPage buildDetailsPage(StackPane content, BookListPanel[] holder, Modele_de_date.Carte book, Button addBtn, Button deleteBtn) {
        return new BookDetailsPage(book, () -> {
            holder[0].refresh();
            addBtn.setVisible(true);
            deleteBtn.setVisible(true);
            content.getChildren().setAll(holder[0]);
        }, editBook -> {
            addBtn.setVisible(false);
            deleteBtn.setVisible(false);
            BookEditInfo editInfo = new BookEditInfo(editBook, () -> {
                content.getChildren().setAll(buildDetailsPage(content, holder, editBook, addBtn, deleteBtn));
            }, savedBook -> {
                content.getChildren().setAll(buildDetailsPage(content, holder, savedBook, addBtn, deleteBtn));
            });
            content.getChildren().setAll(editInfo);
        }, progressBook -> {
            addBtn.setVisible(false);
            deleteBtn.setVisible(false);
            ProgressPage progressPage = new ProgressPage(progressBook, () -> {
                content.getChildren().setAll(buildDetailsPage(content, holder, progressBook, addBtn, deleteBtn));
            }, savedBook -> {
                content.getChildren().setAll(buildDetailsPage(content, holder, savedBook, addBtn, deleteBtn));
            });
            content.getChildren().setAll(progressPage);
        });
    }

    /**
     * Metoda ce creeaza si gestioneaza pagina Statistici si toate sectiunile aferente ei
     * @return ScrollPaneWrapper; pagina de statistici
     */
    private static ScrollPaneWrapper createStatsView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label title = new Label("Statistici");
        title.getStyleClass().add("page-title");

        Region chartsCard = new StatisticsPanel1();
        Region booksPerMonthCard = new StatisticsPanel4();
        Region booksPerYearCard = new StatisticsPanel5();
        Region calendarCard = new StatisticsPanel2();
        Region infoCard = new StatisticsPanel3();

        box.getChildren().addAll(title, chartsCard, booksPerMonthCard, booksPerYearCard, calendarCard, infoCard);
        return new ScrollPaneWrapper(box);
    }

    /**
     * ScrollPane simplu, ca sa nu se taie continutul cand ai multe “carduri”.
     */
    private static class ScrollPaneWrapper extends javafx.scene.control.ScrollPane {
        ScrollPaneWrapper(Pane content) {
            super(content);
            setFitToWidth(true);
            setStyle("-fx-background-color:transparent;");
        }
    }

}

