package jda.modules.mosarfrontend.common.factory;

import lombok.Value;

public class Slot {
    private String slotName;
    private String slotValue;

    public Slot(String slotName, String slotValue) {
        this.slotName = slotName;
        this.slotValue = slotValue;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getSlotValue() {
        return slotValue;
    }

    public void setSlotValue(String slotValue) {
        this.slotValue = slotValue;
    }
}
