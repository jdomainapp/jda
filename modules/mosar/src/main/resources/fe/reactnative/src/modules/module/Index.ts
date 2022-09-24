import { @slot{{importDataType}} } from '../../data_types/@slot{{ModuleName}}';
import { @slot{{ModuleName}}ListConfig } from "./ListConfig";
import { @slot{{ModuleName}}ModuleConfig } from "./ModuleConfig";

@if{haveSubType}((
import { createTypedModuleComponents } from '../../base/creators/createTypedModuleComponents';
import { ITypedFormItem } from "../../base/controllers/jda_form_controllers/withTypedFormController";
import { @slot{{ModuleName}}Type } from "../../data_types/enums/@slot{{ModuleName}}Type";@loop{importSubModuleConfig}[[
import { @slot{{SubModuleName}}Form } from "./sub_modules/@slot{{submoduleFolder}}/Index";]]loop{importSubModuleConfig}@

export const @slot{{ModuleName}}FormList: ITypedFormItem[] = [
  @loop{formTypeItem}[[{
    type: @slot{{EnumType}}Type.@slot{{type}},
    formComponent: @slot{{SubModuleName}}Form
  },]]loop{formTypeItem}@
]

export const {
    Module: @slot{{ModuleName}}Module,
    List: @slot{{ModuleName}}List,
    ListItem: @slot{{ModuleName}}ListItem,
    Form: @slot{{ModuleName}}Form,
} = createTypedModuleComponents<@slot{{importDataType}}>(
    @slot{{ModuleName}}ModuleConfig,
    @slot{{ModuleName}}ListConfig,
    @slot{{ModuleName}}FormList,
);
))if{haveSubType}@
@if{notHaveSubType}((
import { createModuleComponents } from "../../base/creators/createModuleComponents";
import { @slot{{ModuleName}}FormConfig } from "./FormConfig";
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
))if{notHaveSubType}@
