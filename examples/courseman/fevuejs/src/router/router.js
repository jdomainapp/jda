import Vue from 'vue'
import VueRouter from 'vue-router'

//path
import { HOME_PATH } from './path.js'

import { STUDENT_CLASS_INDEX_PATH, STUDENT_CLASS_LIST_PATH, STUDENT_CLASS_ADD_PATH } from './path.js'
import { ADDRESS_INDEX_PATH, ADDRESS_LIST_PATH, ADDRESS_ADD_PATH } from './path.js'
import { COURSE_MODULE_INDEX_PATH, COURSE_MODULE_LIST_PATH, COURSE_MODULE_ADD_PATH } from './path.js'
import { ENROLMENT_INDEX_PATH, ENROLMENT_LIST_PATH, ENROLMENT_ADD_PATH } from './path.js'
import { STUDENT_INDEX_PATH, STUDENT_LIST_PATH, STUDENT_ADD_PATH } from './path.js'

//name
import { HOME_NAME } from './name.js'

import { STUDENT_CLASS_INDEX_NAME, STUDENT_CLASS_LIST_NAME, STUDENT_CLASS_ADD_NAME } from './name.js'
import { ADDRESS_INDEX_NAME, ADDRESS_LIST_NAME, ADDRESS_ADD_NAME } from './name.js'
import { COURSE_MODULE_INDEX_NAME, COURSE_MODULE_LIST_NAME, COURSE_MODULE_ADD_NAME } from './name.js'
import { ENROLMENT_INDEX_NAME, ENROLMENT_LIST_NAME, ENROLMENT_ADD_NAME } from './name.js'
import { STUDENT_INDEX_NAME, STUDENT_LIST_NAME, STUDENT_ADD_NAME } from './name.js'

//component
import { HOME } from './component.js'

import { STUDENT_CLASS_INDEX, STUDENT_CLASS_LIST, STUDENT_CLASS_ADD } from './component.js'
import { ADDRESS_INDEX, ADDRESS_LIST, ADDRESS_ADD } from './component.js'
import { COURSE_MODULE_INDEX, COURSE_MODULE_LIST, COURSE_MODULE_ADD } from './component.js'
import { ENROLMENT_INDEX, ENROLMENT_LIST, ENROLMENT_ADD } from './component.js'
import { STUDENT_INDEX, STUDENT_LIST, STUDENT_ADD } from './component.js'

Vue.use(VueRouter)

export const router = new VueRouter({
    routes: [
        { path: HOME_PATH, name: HOME_NAME, component: HOME },
        
        { path: STUDENT_CLASS_INDEX_PATH, name: STUDENT_CLASS_INDEX_NAME, component: STUDENT_CLASS_INDEX },
        { path: STUDENT_CLASS_LIST_PATH, name: STUDENT_CLASS_LIST_NAME, component: STUDENT_CLASS_LIST },
        { path: STUDENT_CLASS_ADD_PATH, name: STUDENT_CLASS_ADD_NAME, component: STUDENT_CLASS_ADD },
        
        { path: ADDRESS_INDEX_PATH, name: ADDRESS_INDEX_NAME, component: ADDRESS_INDEX },
        { path: ADDRESS_LIST_PATH, name: ADDRESS_LIST_NAME, component: ADDRESS_LIST },
        { path: ADDRESS_ADD_PATH, name: ADDRESS_ADD_NAME, component: ADDRESS_ADD },
        
        { path: COURSE_MODULE_INDEX_PATH, name: COURSE_MODULE_INDEX_NAME, component: COURSE_MODULE_INDEX },
        { path: COURSE_MODULE_LIST_PATH, name: COURSE_MODULE_LIST_NAME, component: COURSE_MODULE_LIST },
        { path: COURSE_MODULE_ADD_PATH, name: COURSE_MODULE_ADD_NAME, component: COURSE_MODULE_ADD },
        
        { path: ENROLMENT_INDEX_PATH, name: ENROLMENT_INDEX_NAME, component: ENROLMENT_INDEX },
        { path: ENROLMENT_LIST_PATH, name: ENROLMENT_LIST_NAME, component: ENROLMENT_LIST },
        { path: ENROLMENT_ADD_PATH, name: ENROLMENT_ADD_NAME, component: ENROLMENT_ADD },
        
        { path: STUDENT_INDEX_PATH, name: STUDENT_INDEX_NAME, component: STUDENT_INDEX },
        { path: STUDENT_LIST_PATH, name: STUDENT_LIST_NAME, component: STUDENT_LIST },
        { path: STUDENT_ADD_PATH, name: STUDENT_ADD_NAME, component: STUDENT_ADD },
        
    ]
})