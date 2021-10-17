package jda.modules.mosar.backend.base.models;

/**
 * Wrapper class to represent a value as an Identifier
 */
public class Identifier<ID> {
    private ID id;

    public Identifier(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public static Identifier<?> fromString(String str) {
        if (str.length() > 9) {
            try {
                return new Identifier<>(Long.parseLong(str));
            } catch (NumberFormatException ex) { }
        } else {
            try {
                return new Identifier<>(Integer.parseInt(str));
            } catch (NumberFormatException ex) { }
        }
        return new Identifier<>(str);
    }
}
