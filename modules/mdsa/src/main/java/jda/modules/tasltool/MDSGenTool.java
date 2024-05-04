package jda.modules.tasltool;

import jda.modules.tasltool.software.MDSoftware;

/**
 * @overview Service Configuration Class (SCL) Generator.
 * SCL is MCC* in the journal paper.
 */
public class MDSGenTool {
    public static void main(String[] args) throws ClassNotFoundException {
        if (args == null || args.length < 1) {
            System.out.println("Usage: " + MDSGenTool.class.getSimpleName() + " <FQN-of-SCC>");
            System.exit(1);
        }

        String sccName = args[0];

        Class scc = Class.forName(sccName);

        new MDSoftware(scc)
                .init()
                .generate();
    }
}
