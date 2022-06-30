export const HOME_NAME = 'home';
@loop{MODULE_NAME&module_name}[[
export const @slot{{MODULE_NAME}}_INDEX_NAME = '@slot{{module_name}}';
export const @slot{{MODULE_NAME}}_LIST_NAME = '@slot{{module_name}}-list';
export const @slot{{MODULE_NAME}}_ADD_NAME = '@slot{{module_name}}-add';
export const @slot{{MODULE_NAME}}_EDIT_NAME = '@slot{{module_name}}-edit';]]loop{MODULE_NAME&module_name}@