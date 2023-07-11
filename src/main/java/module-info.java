module com.example.forensic_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.forensic_project to javafx.fxml;
    exports com.example.forensic_project;
}