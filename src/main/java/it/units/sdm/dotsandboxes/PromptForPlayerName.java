package it.units.sdm.dotsandboxes;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.InputResult;

import java.io.IOException;

public class PromptForPlayerName {
    private final ConsolePrompt prompt;

    public PromptForPlayerName(ConsolePrompt prompt) {
        this.prompt = prompt;
    }

    public String getPlayerName(int playerNumber){
        String name = "";
        do {
            String promptName = "name" + playerNumber;
            try {
                InputResult ir = getInputResult(playerNumber, promptName);
                name = ir.getInput();
            } catch (IOException ignored) {}
        } while (name == null || name.isEmpty());
        return name;
    }

    private InputResult getInputResult(int playerNumber, String promptName) throws IOException {
        return (InputResult) prompt.prompt(
                prompt.getPromptBuilder().createInputPrompt()
                        .name(promptName)
                        .defaultValue("Player " + playerNumber)
                        .message("Name for player #" + playerNumber)
                        .addPrompt().build()
        ).get(promptName);
    }
}
