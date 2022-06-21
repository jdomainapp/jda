import { createModuleComponents } from "../../../../base/creators/createModuleComponents";
import { @slot{{importDataType}} } from '../../../../data_types/@slot{{SubModuleName}}';
import { @slot{{SubModuleName}}FormConfig } from "./FormConfig";
import { @slot{{SubModuleName}}ListConfig } from "./ListConfig";
import { @slot{{SubModuleName}}ModuleConfig } from "./ModuleConfig";

export const {
    Module: @slot{{SubModuleName}}Module,
    List: @slot{{SubModuleName}}List,
    ListItem: @slot{{SubModuleName}}ListItem,
    Form: @slot{{SubModuleName}}Form,
} = createModuleComponents<@slot{{SubModuleName}}>(
    @slot{{SubModuleName}}ModuleConfig,
    @slot{{SubModuleName}}ListConfig,
    @slot{{SubModuleName}}FormConfig,
);
