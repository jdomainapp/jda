export const HOME_PATH = '/';
@loop{MODULE_NAME&moduleJname}[[
export const @slot{{MODULE_NAME}}_INDEX_PATH = '/@slot{{moduleJname}}';
export const @slot{{MODULE_NAME}}_LIST_PATH = '/@slot{{moduleJname}}-list';
export const @slot{{MODULE_NAME}}_ADD_PATH = '/@slot{{moduleJname}}-add';]]loop{MODULE_NAME&moduleJname}@