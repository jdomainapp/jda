export const HOME = () => import('../components/home/home.vue');
@loop{MODULE_NAME&moduleJname}[[
export const @slot{{MODULE_NAME}}_INDEX = () => import('../components/@slot{{moduleJname}}/index.vue');
export const @slot{{MODULE_NAME}}_LIST = () => import('../components/@slot{{moduleJname}}/list.vue');
export const @slot{{MODULE_NAME}}_ADD = () => import('../components/@slot{{moduleJname}}/add.vue');
]]loop{MODULE_NAME&moduleJname}@