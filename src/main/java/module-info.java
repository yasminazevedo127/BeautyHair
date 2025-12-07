/**
 * 
 */
/**
 * 
 */
module BeautyHair {
	requires transitive javafx.base;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.google.gson;

    opens control to javafx.fxml;
    opens model to com.google.gson;
    opens repository to com.google.gson;

    exports control;
    exports model;
    exports repository;
    exports application to javafx.graphics;
}