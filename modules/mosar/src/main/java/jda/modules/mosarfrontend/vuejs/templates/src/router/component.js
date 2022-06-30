export const HOME = () => import('../components/home/home.vue');
@loop{MODULE_NAME&module_name}[[
export const @slot{{MODULE_NAME}}_INDEX = () => import('../components/@slot{{module_name}}/index.vue');
export const @slot{{MODULE_NAME}}_LIST = () => import('../components/@slot{{module_name}}/edit.vue');
export const @slot{{MODULE_NAME}}_ADD = () => import('../components/@slot{{module_name}}/list.vue');
export const @slot{{MODULE_NAME}}_EDIT = () => import('../components/@slot{{module_name}}/add.vue');
]]loop{MODULE_NAME&module_name}@