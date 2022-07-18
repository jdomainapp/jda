package jda.modules.mosarfrontend.vuejs.src.router;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;

@FileTemplateDesc(
        templateFile = "/src/router/router.js"
)
public class routerGen extends BaseRouterGen {
    @LoopReplacement(id = "importPaths", slots = {"MODULE_NAME"})
    public Slot[][] importPaths(@RequiredParam.ModulesName String[] modulesName) {
        return  super.MODULE_NAME(modulesName);
    }

    @LoopReplacement(id = "importNames", slots = {"MODULE_NAME"})
    public Slot[][] importNames(@RequiredParam.ModulesName String[] modulesName) {
        return  super.MODULE_NAME(modulesName);
    }

    @LoopReplacement(id = "importComponents", slots = {"MODULE_NAME"})
    public Slot[][] importComponents(@RequiredParam.ModulesName String[] modulesName) {
        return  super.MODULE_NAME(modulesName);
    }

    @LoopReplacement(id = "routeDeclarations", slots = {"MODULE_NAME"})
    public Slot[][] routeDeclarations(@RequiredParam.ModulesName String[] modulesName) {
        return  super.MODULE_NAME(modulesName);
    }
}
