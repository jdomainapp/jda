@loop{import}[[import { @slot{{importModuleName}} } from "./@slot{{importModuleName}}"
]]loop{import}@
export interface @slot{{moduleName}} {@loop{1}[[
  @slot{{field}}: @slot{{fieldType}};]]loop{1}@
}