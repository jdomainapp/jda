@loop{importEnum}[[import {@slot{{importEnumName}}} from './@slot{{importEnumName}}';
]]loop{importEnum}@
@loop{import}[[import {Sub@slot{{importModuleName}}} from './@slot{{importModuleName}}';
]]loop{import}@
export interface @slot{{moduleName}} {@loop{1}[[
  @slot{{field}}: @slot{{fieldType}};]]loop{1}@
}
@if{subtype}((
export interface Sub@slot{{moduleName}} extends Omit<@slot{{moduleName}},@loop{domainFields}[[| '@slot{{domainFieldName}}' ]]loop{domainFields}@> {@loop{subInterface}[[
  @slot{{field}}: @slot{{fieldType}};]]loop{subInterface}@
}))if{subtype}@
