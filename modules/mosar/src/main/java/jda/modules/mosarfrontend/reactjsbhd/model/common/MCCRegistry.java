package jda.modules.mosarfrontend.reactjsbhd.model.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jda.modules.mccl.conceptualmodel.MCC;

public class MCCRegistry {
    private final Map<String, MCC> mccCompilationUnits = new HashMap<>();

    private MCCRegistry() { }

    public synchronized void add(MCC mcc) {
        this.mccCompilationUnits.put(mcc.getDomainClass().getName(), mcc);
    }

    public MCC getByName(String name) {
        return mccCompilationUnits.get(name);
    }

    private static MCCRegistry instance;

    public synchronized static MCCRegistry getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MCCRegistry();
        }
        return instance;
    }
}
