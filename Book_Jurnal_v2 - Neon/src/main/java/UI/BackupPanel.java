package UI;

import Baza_de_date.BDConnectionManager;
import Services.BackupImportService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;

/**
 * Clasa ce are ca rol crearea si gestionarea butoanelor de export/import
 */
public class BackupPanel extends VBox {

    private static final Path BOOKS_CSV_PATH = Path.of("data\\cartiExport.csv");
    private static final Path JURNAL_JSON_PATH = Path.of("data\\jurnalExport.json");
    private static final Path LOG_PATH = Path.of("data\\ui.log");

    /**
     * Metoda ce gestioneaza zona de backup - import din pagina Home
     */
    public BackupPanel() {
        setSpacing(12);
        setPadding(new Insets(16));
        setStyle("""
                -fx-background-color: white;
                -fx-border-color: #dddddd;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                """);

        Label title = new Label("Backup & Import");
        title.setFont(Font.font(16));
        title.setStyle("-fx-font-weight: bold;");

        Label subtitle = new Label("Exporta fisierele local si importa in baza de date.");
        subtitle.setStyle("-fx-opacity: 0.8;");

        Button btnBackupBooksCsv = new Button("Backup Books (BD -> CSV)");
        Button btnBackupJurnalJson = new Button("Backup Reading Journal (BD -> JSON)");
        Button btnImportJurnalJson = new Button("Import Reading Journal (JSON -> DB)");
        Button btnImportBooksCsv = new Button("Import Books (CSV -> DB)");

        btnBackupBooksCsv.setMaxWidth(Double.MAX_VALUE);
        btnBackupJurnalJson.setMaxWidth(Double.MAX_VALUE);
        btnImportJurnalJson.setMaxWidth(Double.MAX_VALUE);
        btnImportBooksCsv.setMaxWidth(Double.MAX_VALUE);

        btnBackupBooksCsv.setOnAction(e -> {
            try {
                safeRun("Backup Books CSV", this::onBackupBooksCsv);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnBackupJurnalJson.setOnAction(e -> {
            try {
                safeRun("Backup Journal JSON", this::onBackupJurnalJson);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnImportJurnalJson.setOnAction(e -> {
            try {
                safeRun("Import Journal JSON", this::onImportJurnalJsonToDb);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnImportBooksCsv.setOnAction(e -> {
            try {
                safeRun("Import Books CSV", this::onImportBooksCsvToDb);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        HBox pathInfo = new HBox(10, new Label("CSV: " + BOOKS_CSV_PATH), new Label("JSON: " + JURNAL_JSON_PATH));
        pathInfo.setAlignment(Pos.CENTER_LEFT);
        pathInfo.setStyle("-fx-opacity: 0.75; -fx-font-size: 11px;");
        getChildren().addAll(title, subtitle, pathInfo, btnBackupBooksCsv, btnBackupJurnalJson, new Separator(), btnImportBooksCsv, btnImportJurnalJson);
    }

    /**
     * Butonul care este responsabil pentru exportul cartilor  in CSV
     * @throws Exception se arunca exceptie daca fisierul de backup nu exista sau exportul esueaza
     */
    private void onBackupBooksCsv() throws Exception {
        if (!Files.exists(BOOKS_CSV_PATH)) {
            throw new IOException("Fișierul CSV nu exista: " + BOOKS_CSV_PATH);
        }

        try (Connection conn = BDConnectionManager.getConnection()) {
            int nr = BackupImportService.exportBooksToCSV(BOOKS_CSV_PATH.toString());
            log("Books exportat in: " + BOOKS_CSV_PATH + " (" + nr + " rows)");
        }
    }

    /**
     * Butonul care este responsabil pentru exportul datelor legate de intrarile in jurnal in JSON
     * @throws Exception se arunca daca fisierul de export nu exista sau exportul esueaza
     */
    private void onBackupJurnalJson() throws Exception {
        if (!Files.exists(JURNAL_JSON_PATH)) {
            throw new IOException("Fișierul JSON nu exista: " + JURNAL_JSON_PATH);
        }

        try (Connection conn = BDConnectionManager.getConnection()) {
            int nr = BackupImportService.exportReadingLogToJSON(JURNAL_JSON_PATH.toString(), conn);
            log("Jurnal exportat in: " + JURNAL_JSON_PATH + " (" + nr + " rows)");
        }
    }

    /**
     * Butonul care este responsabil pentru importul datelor legate de carti din CSV
     * @throws Exception se arunca daca fisierul de import nu exista sau importul esueaza
     */
    private void onImportBooksCsvToDb() throws Exception {
        if (!Files.exists(BOOKS_CSV_PATH)) {
            throw new IOException("Fișierul CSV nu exista: " + BOOKS_CSV_PATH);
        }

        try (Connection conn = BDConnectionManager.getConnection()) {
            int nr = BackupImportService.importCartiCSV_InDB( BOOKS_CSV_PATH.toString(),  conn);
            log("Books importat in DB din: " + BOOKS_CSV_PATH + " (" + nr + " rows)");
        }
    }

    /**
     * Butonul care este responsabil pentru importul datelor legate de intrarile in jurnal din JSON
     * @throws Exception se arunca o exceptie daca fisierul de import nu exista sau importul esueaza
     */
    private void onImportJurnalJsonToDb() throws Exception {
        if (!Files.exists(JURNAL_JSON_PATH)) {
            throw new IOException("Fișierul JSON nu exista: " + JURNAL_JSON_PATH);
        }

        try (Connection conn = BDConnectionManager.getConnection()) {
            int nr = BackupImportService.importReadingLogDinJSON_InDB( JURNAL_JSON_PATH.toString(), conn );
            log("Jurnal importat in DB din: " + JURNAL_JSON_PATH + " (" + nr + " rows)");
        }
    }

    /**
     * Metoda ce incearca sa ruleze o actiune a utilizatorului si scrie un mesaj in log. Daca apare exceptie, logheaza eroarea, afiseaza stack trace in terminal si arata un dialog de eroare.
     * @param actionName String, numele actiunii ce urmeaza a fi rulata
     * @param r ThrowingRunnable, blocul de cod care se executa
     */
    private void safeRun(String actionName, ThrowingRunnable r) throws IOException {
        try {
            log("-> " + actionName + "...");
            r.run();
        } catch (Exception ex) {
            log("Eroare: " + ex.getMessage());
            ex.printStackTrace();
            showError(actionName, ex);
        }
    }

    /**
     * Metoda care are ca scop afisarea unei ferestre de erori pentru utilizator
     * @param action String, actiunea care a esuat
     * @param ex Exception, exceptia aruncata de program
     */
    private void showError(String action, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText(action);
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }

    /**
     * Metoda ca are ca rol scrierea in log si afisarea in consola
     * @param msg String, mesajul care este scris in log
     */
    private void log(String msg) throws IOException {
        System.out.println(msg);
        String line = msg + System.lineSeparator();

        if (!Files.exists(LOG_PATH)) {
            Path parent = LOG_PATH.getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        }

        try {
            Files.writeString(LOG_PATH, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }

    /**
     * Interfata functionala pentru actiuni care pot arunca exceptii
     */
    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}

