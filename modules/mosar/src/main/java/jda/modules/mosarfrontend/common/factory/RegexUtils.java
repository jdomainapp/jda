package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosarfrontend.common.anotation.LoopReplacement;

import java.util.regex.Pattern;

public class RegexUtils {
    public static Pattern createSlotRegex(String slot) {
        return Pattern.compile(String.format("@slot\\{\\{\\s*%s\\s*\\}\\}", slot), Pattern.DOTALL);
    }

    public static Pattern createLoopRegex(LoopReplacement loop) {
        StringBuilder singleSlotsRegex = new StringBuilder();
        for (String slot : loop.slots()) {
            singleSlotsRegex.append(createSlotRegex(slot));
            singleSlotsRegex.append(".*");
        }
        return Pattern.compile(String.format("@loop(?<li>\\{%s})\\[\\[(.*)]]loop(\\k<li>)@", loop.id()), Pattern.DOTALL);
    }

    public static Pattern createIfRegex(String id) {
        return Pattern.compile(String.format("@if(?<li>\\{%s\\})\\(\\((.*)\\)\\)if(\\k<li>)@", id), Pattern.DOTALL);
    }
}
