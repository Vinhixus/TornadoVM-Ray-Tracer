package com.vinhderful.raytracer.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

/**
 * Controller class for About window
 */
public class About {

    /**
     * JavaFX GUI elements
     */
    @FXML
    public Hyperlink email;
    public Hyperlink linkedIn;

    /**
     * Open a given uri
     *
     * @param uri uri string
     */
    private void openURL(String uri) {
        // TODO implement code to open URIs
    }

    /**
     * Open LinkedIn page
     */
    public void openLinkedIn() {
        openURL("https://www.linkedin.com/in/" + linkedIn.getText() + "/");
    }

    /**
     * Open email
     */
    public void openEmail() {
        openURL("mailto:" + email.getText());
    }
}
