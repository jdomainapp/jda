package jda.modules.mosarfrontend.common.anotation;

import lombok.Data;

@Data
public class SlotReplacement {
    private String slot;

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }
}
