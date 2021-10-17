package jda.modules.mosar.backend.base.websockets;

public class Change {
    private final String change;
    private final Object content;

    public Change(final String change, final Object content) {
        this.change = change;
        this.content = content;
    }

    public String getChange() {
        return change;
    }

    public Object getContent() {
        return content;
    }

    public static Change defaultChange() {
        return new Change(
                "",
                ""
        );
    }
}