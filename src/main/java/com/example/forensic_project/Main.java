package com.example.forensic_project;

import FilesReaders.RecoveredFiles;
import FilesReaders.TargetFile;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        TextField size = new TextField();
        size.setMaxWidth(100);
        size.setPromptText("Enter size of fragment in KB");

        TextField matchingTextField = new TextField();
        size.setPromptText("Matching percent");
        matchingTextField.setEditable(false);
        matchingTextField.setMaxWidth(100);

        GridPane pane = new GridPane();
        pane.add(new Label("Fragment size in KB:"), 0, 0);
        pane.add(size, 1, 0);
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);


        GridPane pane2 = new GridPane();
        pane2.add(new Label("Result:"), 0, 0);
        pane2.add(matchingTextField, 1, 0);
        pane2.setAlignment(Pos.CENTER);
        pane2.setHgap(10);
        pane2.setVgap(10);


        Button targetButton = new Button("Select target file");
        targetButton.setDisable(true);

        size.setOnKeyReleased(e -> targetButton.setDisable(size.getText().isEmpty()));

        Button recoveredButton = new Button("Select recovered from image folder");
        recoveredButton.setDisable(true);

        VBox box = new VBox(20, pane, targetButton, recoveredButton, pane2);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(50));
        box.setBackground(new Background(new BackgroundFill(Color.SKYBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        stage.setTitle("Forensic Project");
        Scene scene = new Scene(box, 1400, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        targetButton.setOnAction(e -> {
            if (!size.getText().isEmpty()) {
                try {

                    TargetFile targetFile = new TargetFile(fileChooser.showOpenDialog(stage), Integer.parseInt(size.getText()) * 1024);

                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setInitialDirectory(new File("C:\\Users\\user\\PycharmProjects\\forensic"));

                    recoveredButton.setDisable(false);
                    size.setEditable(false);
                    targetButton.setDisable(true);

                    recoveredButton.setOnAction(e1 -> {
                        targetButton.setDisable(true);
                        recoveredButton.setDisable(true);
                        File directory = directoryChooser.showDialog(stage);
                        if (directory == null)
                            recoveredButton.setDisable(false);
                        else {
                            File[] files = directory.listFiles();
                            RecoveredFiles recoveredFiles = new RecoveredFiles(files, targetFile.getFileType(), targetFile.getSize());

                            String matchingStr = percentOfMatching(targetFile.getTargetHashing(), recoveredFiles.getRecoveredHashing(), box);
                            matchingTextField.setText(matchingStr);
                        }
                    });
                } catch (NumberFormatException e1) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid size");
                    alert.setContentText("Please enter a valid size");
                    alert.show();
                } catch (FileNotFoundException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid file");
                    alert.setContentText("Please select a valid file");
                    alert.show();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error while writing to file");
                    alert.setContentText("Please try again");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid size");
                alert.setContentText("Please enter a valid size");
                alert.show();
            }
        });
    }


    String percentOfMatching(ObservableList<String> targetHashing, ObservableList<String> recoveredHashing, VBox box) {
        int matching = 0;
        int size = 0;
        ObservableList<String> matchingList = FXCollections.observableArrayList();
        ObservableList<String> nonMatchingList = FXCollections.observableArrayList();
        for (String s : targetHashing) {
            char[] ch = s.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : ch) {
                String hexString = Integer.toHexString(c).toUpperCase();
                sb.append(hexString);
            }
            if (recoveredHashing.contains(s)) {
                matching++;
                matchingList.add(sb.toString());
            } else
                nonMatchingList.add(sb.toString());
            size++;
        }

        Label matchLabel = new Label("Match:");
        ListView<String> matchListView = new ListView<>(matchingList);
        VBox match = new VBox(10, matchLabel, matchListView);
        match.setAlignment(Pos.CENTER);

        Label nonMatchLabel = new Label("NOT Matching:");
        ListView<String> nonMatchListView = new ListView<>(nonMatchingList);
        VBox nonMatch = new VBox(10, nonMatchLabel, nonMatchListView);
        nonMatch.setAlignment(Pos.CENTER);

        HBox all = new HBox(10, match, nonMatch);
        all.setAlignment(Pos.CENTER);
        if (box.getChildren().size() == 4)
            box.getChildren().add(all);
        else
            box.getChildren().set(4, all);
        return (double) matching / size * 100 + "%";
    }

    public static void main(String[] args) {
        launch();
    }
}