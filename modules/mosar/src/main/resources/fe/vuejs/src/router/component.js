export const HOME = () => import('../components/home/home.vue');
@loop{MODULE_NAME&moduleJname}[[
export const @slot{{MODULE_NAME}}_INDEX = () => import('../components/@slot{{module_name}}/index.vue');
export const @slot{{MODULE_NAME}}_LIST = () => import('../components/@slot{{module_name}}/edit.vue');
export const @slot{{MODULE_NAME}}_ADD = () => import('../components/@slot{{module_name}}/list.vue');
]]loop{MODULE_NAME&moduleJname}@