package de.lsoft.home.tgraph;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main extends Application {

    public static void main(String[] args) {
        //Set the look and feel to users OS LaF.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Willkommen");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Willkommen");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label datei = new Label("Datei:");
        grid.add(datei, 0, 1);

        final File[] selectedFile = {null};
        Button btn = new Button("Auswerten");

        Button dateiFileChoose = new Button("Auswählen");
        dateiFileChoose.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Auswaehlen");
            selectedFile[0] = fileChooser.showOpenDialog(primaryStage);
            btn.setDisable(selectedFile[0] == null);
        });
        grid.add(dateiFileChoose, 1, 1);

        Label start = new Label("Start Intervall:");
        grid.add(start, 0, 2);

        DatePicker datePickerStart = new DatePicker();
        grid.add(datePickerStart, 1, 2);


        Label end = new Label("Start Intervall:");
        grid.add(end, 0, 3);

        DatePicker datePickerEnd = new DatePicker();
        grid.add(datePickerEnd, 1, 3);

        Label fehlerLabel = new Label("Fehlertypen:");
        grid.add(fehlerLabel, 0, 4);

        TextField fehlerField = new TextField("R006");
        grid.add(fehlerField, 1, 4);


        btn.setDisable(true);
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 6);

        TableView table = new TableView();
        table.setEditable(true);

        TableColumn errorAt = new TableColumn("Fehlerzeitpunkt");
        errorAt.setCellValueFactory(
                new PropertyValueFactory<>("fehlerzeitpunkt"));
        TableColumn fehler = new TableColumn("Fehlertyp");
        fehler.setCellValueFactory(new PropertyValueFactory<>("fehlertyp"));

        table.getColumns().addAll(errorAt, fehler);

        StackPane root = new StackPane();
        root.getChildren().add(table);
        root.setVisible(false);
        grid.add(root, 0, 7, 2, 5);



        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
        btn.setOnAction(event -> {
            if (selectedFile[0] != null) {
                try {
                    LocalDate valueStart = datePickerStart.getValue();
                    LocalDate valueEnd = datePickerEnd.getValue();

                    TreeMap<LocalDateTime, String> map = new FileReader().readFile(selectedFile[0].getAbsolutePath(), valueStart, valueEnd, fehlerField.getText().split(";"));
                    List<Fehler> fehlerList = new ArrayList<>();
                    for (Map.Entry<LocalDateTime, String> entry : map.entrySet()) {
                        fehlerList.add(new Fehler(entry.getKey().toString(), entry.getValue()));
                    }

                    showChart(map, datePickerStart.getValue(), datePickerEnd.getValue());
                    ObservableList<Fehler> data = FXCollections.observableArrayList(fehlerList);
                    table.setItems(data);
                    root.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showChart(TreeMap<LocalDateTime, String> map, LocalDate von, LocalDate bis) throws IOException {


        final String title = "Abbrüche Telekom";
        final Chart demo = new Chart(title, map, von, bis);
        demo.pack();
        demo.setVisible(true);
    }


    public static class Fehler {

        private final SimpleStringProperty fehlerzeitpunkt;
        private final SimpleStringProperty fehlertyp;


        private Fehler(String fehlerzeitpunkt, String lastName) {
            this.fehlerzeitpunkt = new SimpleStringProperty(fehlerzeitpunkt);
            this.fehlertyp = new SimpleStringProperty(lastName);
        }

        public String getFehlerzeitpunkt() {
            return fehlerzeitpunkt.get();
        }

        public void setFehlerzeitpunkt(String fName) {
            fehlerzeitpunkt.set(fName);
        }

        public String getFehlertyp() {
            return fehlertyp.get();
        }

        public void setFehlertyp(String fName) {
            fehlertyp.set(fName);
        }
    }
}