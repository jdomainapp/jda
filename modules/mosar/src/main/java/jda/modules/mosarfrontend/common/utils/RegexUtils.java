package jda.modules.mosarfrontend.common.utils;

import java.util.regex.Pattern;

public class RegexUtils {
    public static Pattern createSlotRegex(String slot) {
        return Pattern.compile(String.format("@slot\\{\\{\\s*%s\\s*\\}\\}", slot), Pattern.DOTALL);
    }

    public static Pattern createLoopRegex(String id) {
        StringBuilder singleSlotsRegex = new StringBuilder();
        return Pattern.compile(String.format("@loop(?<li>\\{%s})\\[\\[(.*)]]loop(\\k<li>)@", id), Pattern.DOTALL);
    }

    public static Pattern createIfRegex(String id) {
        return Pattern.compile(String.format("@if(?<li>\\{%s\\})\\(\\((.*)\\)\\)if(\\k<li>)@", id), Pattern.DOTALL);
    }
}
