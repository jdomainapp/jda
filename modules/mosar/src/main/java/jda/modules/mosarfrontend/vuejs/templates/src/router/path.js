export const HOME_PATH = '/';
@loop{MODULE_NAME&module_name}[[
export const @slot{{MODULE_NAME}}_INDEX_PATH = '/@slot{{module_name}}';
export const @slot{{MODULE_NAME}}_EDIT_PATH = '/@slot{{module_name}}-edit';
export const @slot{{MODULE_NAME}}_LIST_PATH = '/@slot{{module_name}}-list';
export const @slot{{MODULE_NAME}}_ADD_PATH = '/@slot{{module_name}}-add';]]loop{MODULE_NAME&module_name}@