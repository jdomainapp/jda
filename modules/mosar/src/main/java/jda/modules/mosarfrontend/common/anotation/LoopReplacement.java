package jda.modules.mosarfrontend.common.anotation;

import lombok.Data;

@Data
public class LoopReplacement {
    private String[] slots;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getSlots() {
        return slots;
    }

    public void setSlots(String[] slots) {
        this.slots = slots;
    }
}
