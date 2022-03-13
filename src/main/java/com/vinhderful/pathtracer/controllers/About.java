package com.vinhderful.pathtracer.controllers;

import javafx.scene.control.Hyperlink;

public class About {

    public Hyperlink email;
    public Hyperlink linkedIn;

    private void openURL(String uri) {
        // TODO implement code to open URIs
    }

    public void openLinkedIn() {
        openURL("https://www.linkedin.com/in/" + linkedIn.getText() + "/");
    }

    public void openEmail() {
        openURL("mailto:" + email.getText());
    }
}
