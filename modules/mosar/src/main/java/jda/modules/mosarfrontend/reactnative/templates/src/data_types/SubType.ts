import { @slot{{ModuleName}} } from './@slot{{ModuleName}}';

export interface @slot{{SubModuleName}} extends @slot{{ModuleName}} {@loop{fields}[[
    @slot{{field}}: @slot{{fieldType}};]]loop{fields}@
}