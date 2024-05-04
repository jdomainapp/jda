package jda.modules.tasltool;

//import jda.modules.tmsa.tasl.conceptmodel.msaservice.MsaService;

import jda.modules.tasltool.software.MDSoftware;

public class MDSRunTool {
    public static void main(String[] args) throws ClassNotFoundException {
        if (args == null || args.length < 1) {
            System.out.println("Usage: " + MDSRunTool.class.getSimpleName() + " <FQN-of-SCC>");
            System.exit(1);
        }

        String sccName = args[0];

        Class scc = Class.forName(sccName);

        new MDSoftware(scc)
                .init()
                .run();
    }
}
