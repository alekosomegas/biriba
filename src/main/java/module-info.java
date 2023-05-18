module com.akgames.biriba {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.akgames.biriba to javafx.fxml;
    exports com.akgames.biriba;
}