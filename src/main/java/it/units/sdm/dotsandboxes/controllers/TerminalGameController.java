package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedSave;
import it.units.sdm.dotsandboxes.views.IGameView;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.views.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TerminalGameController extends IGameController {


    public TerminalGameController(IGameView view) {
        super(view);
    }

    public TerminalGameController() {
        this.view = new TextView();
    }


    @Override
    public boolean initialize() {
        isInitialized = view.init(this);
        return isInitialized;
    }


}
