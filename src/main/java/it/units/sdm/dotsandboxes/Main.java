package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.RandomGameController;
import it.units.sdm.dotsandboxes.controllers.ShellGameController;
import it.units.sdm.dotsandboxes.views.ShellView;

public class Main {

    public static void main(String[] args) {
        new GameSession(new RandomGameController(),new ShellView()).start();
    }
}
