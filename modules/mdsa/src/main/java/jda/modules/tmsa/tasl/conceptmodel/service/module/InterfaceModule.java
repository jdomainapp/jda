package jda.modules.tmsa.tasl.conceptmodel.service.module;

import jda.modules.tmsa.tasl.conceptmodel.service.Service;

public class InterfaceModule extends Module {
    private Service service;

    public InterfaceModule(Model model) {
        super(model);
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
