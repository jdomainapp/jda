export const HOME_NAME = 'home';
@loop{MODULE_NAME&moduleJname}[[
export const @slot{{MODULE_NAME}}_INDEX_NAME = '@slot{{moduleJname}}';
export const @slot{{MODULE_NAME}}_LIST_NAME = '@slot{{moduleJname}}-list';
export const @slot{{MODULE_NAME}}_ADD_NAME = '@slot{{moduleJname}}-add';
export const @slot{{MODULE_NAME}}_EDIT_NAME = '@slot{{moduleJname}}-edit';]]loop{MODULE_NAME&moduleJname}@