package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.views.IGameView;
import it.units.sdm.dotsandboxes.views.SwingView;

public class SwingGameController extends IGameController{

    public SwingGameController(IGameView view) {
        super(view);
    }

    public SwingGameController() {
        super(new SwingView());
    }

    @Override
    public boolean initialize() {
        isInitialized = view.init(this);
        return isInitialized;
    }
}
