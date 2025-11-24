/**
 * 
 */
/**
 * 
 */
module BeutyHAir {
	requires javafx.controls;
    requires javafx.fxml;

    requires com.google.gson;

    opens control to javafx.fxml;
    opens model to com.google.gson; 
    opens view;
    
    exports application;
}