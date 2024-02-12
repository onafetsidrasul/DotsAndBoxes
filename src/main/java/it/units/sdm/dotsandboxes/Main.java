package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.ShellGameController;

public class Main {

    public static void main(String[] args) {
        GameSession.start(new ShellGameController());
    }

}
