package eu.quelltext.wget.bin.wget.options;

import org.json.JSONException;
import org.json.JSONObject;

import eu.quelltext.wget.bin.wget.options.display.Display;
import eu.quelltext.wget.bin.wget.options.display.Strategy;
import eu.quelltext.wget.bin.wget.options.display.DisplayableOption;

public class ArgumentOptionBuilder implements Options.Manual.ManualEntry, DisplayableOption {

    public static final String JSON_ARGUMENT = "argument";

    private final String cmd;
    private final int nameId;
    private final int explanationId;
    private final Strategy displayStrategy;

    public ArgumentOptionBuilder(String cmd, int nameId, int explanationId, Strategy displayStrategy) {
        super();
        this.cmd = cmd;
        this.nameId = nameId;
        this.explanationId = explanationId;
        this.displayStrategy = displayStrategy;
    }

    public ArgumentOption to(String argument) {
        return new ArgumentOption(this.cmd, this.nameId, this.explanationId, argument);
    }

    public int getNameId() {
        return nameId;
    }

    public int getExplanationId() {
        return explanationId;
    }

    public Strategy getDisplayStrategy() {
        return displayStrategy;
    }

    @Override
    public String manualId() {
        return cmd;
    }

    @Override
    public Option fromManualJSON(JSONObject json) throws JSONException {
        String argument = json.getString(JSON_ARGUMENT);
        Option option = to(argument);
        return option;
    }

    @Override
    public void fillWith(Display display, Option option) {
        display.switchOn();
        String argument = option.getArgument();
        displayStrategy.setArgumentIn(display, argument);
    }

    @Override
    public Option createNewFrom(Display display) {
        return to(displayStrategy.getArgument(display));
    }

    @Override
    public void displayIn(Display section) {
        section.addSwitch();
        section.addTitle(getNameId());
        section.addExplanation(getExplanationId());
        displayStrategy.displayIn(section);
    }

    public Option defaultOption() {
        return to(getDisplayStrategy().getDefaultArgument());
    }
}
