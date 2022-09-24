import Vue from 'vue'
import VueRouter from 'vue-router'

//path
import { HOME_PATH } from './path.js'
@loop{importPaths}[[
import { @slot{{MODULE_NAME}}_INDEX_PATH, @slot{{MODULE_NAME}}_LIST_PATH, @slot{{MODULE_NAME}}_ADD_PATH } from './path.js']]loop{importPaths}@

//name
import { HOME_NAME } from './name.js'
@loop{importNames}[[
import { @slot{{MODULE_NAME}}_INDEX_NAME, @slot{{MODULE_NAME}}_LIST_NAME, @slot{{MODULE_NAME}}_ADD_NAME } from './name.js']]loop{importNames}@

//component
import { HOME } from './component.js'
@loop{importComponents}[[
import { @slot{{MODULE_NAME}}_INDEX, @slot{{MODULE_NAME}}_LIST, @slot{{MODULE_NAME}}_ADD } from './component.js']]loop{importComponents}@

Vue.use(VueRouter)

export const router = new VueRouter({
    routes: [
        { path: HOME_PATH, name: HOME_NAME, component: HOME },
        @loop{routeDeclarations}[[
        { path: @slot{{MODULE_NAME}}_INDEX_PATH, name: @slot{{MODULE_NAME}}_INDEX_NAME, component: @slot{{MODULE_NAME}}_INDEX },
        { path: @slot{{MODULE_NAME}}_LIST_PATH, name: @slot{{MODULE_NAME}}_LIST_NAME, component: @slot{{MODULE_NAME}}_LIST },
        { path: @slot{{MODULE_NAME}}_ADD_PATH, name: @slot{{MODULE_NAME}}_ADD_NAME, component: @slot{{MODULE_NAME}}_ADD },
        ]]loop{routeDeclarations}@
    ]
})