@loop{importLinkedModules}[[
import { @slot{{LinkedName}} } from "src/app/@slot{{linkedJname}}/models/@slot{{linkedJname}}";
]]loop{importLinkedModules}@

export class @slot{{ModuleName}} {
    @loop{fields}[[
    @slot{{fieldName}}?: @slot{{fieldType}};]]loop{fields}@
}
