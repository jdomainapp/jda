import { createModuleComponents } from "../../base/creators/createModuleComponents";
import {@slot{{importDataType}}} from '../../data_types/@slot{{ModuleName}}';
import { @slot{{ModuleName}}FormConfig } from "./FormConfig";
import { @slot{{ModuleName}}ListConfig } from "./ListConfig";
import { @slot{{ModuleName}}ModuleConfig } from "./ModuleConfig";

export const {
    Module: @slot{{ModuleName}}Module,
    List: @slot{{ModuleName}}List,
    ListItem: @slot{{ModuleName}}ListItem,
    Form: @slot{{ModuleName}}Form,
} = createModuleComponents<@slot{{importDataType}}>(
    @slot{{ModuleName}}ModuleConfig,
    @slot{{ModuleName}}ListConfig,
    @slot{{ModuleName}}FormConfig,
);