import {
    HOME_NAME,
    STUDENT_CLASS_INDEX_NAME,
    ADDRESS_INDEX_NAME,
    COURSE_MODULE_INDEX_NAME,
    ENROLMENT_INDEX_NAME,
    STUDENT_INDEX_NAME,

    // demo design pattern
    DEMO_NAME,
} from "./name.js";

// Nguyen Dang Duc Anh, 02/12/2023: Em viết lại string của các const để thêm từ name.js vào.

export const HOME = () => import(`../components/${HOME_NAME}/home.vue`);

export const STUDENT_CLASS_INDEX = () =>
    import(`../components/${STUDENT_CLASS_INDEX_NAME}/index.vue`);
export const STUDENT_CLASS_LIST = () =>
    import(`../components/${STUDENT_CLASS_INDEX_NAME}/list.vue`);
export const STUDENT_CLASS_ADD = () =>
    import(`../components/${STUDENT_CLASS_INDEX_NAME}/add.vue`);

export const ADDRESS_INDEX = () =>
    import(`../components/${ADDRESS_INDEX_NAME}/index.vue`);
export const ADDRESS_LIST = () =>
    import(`../components/${ADDRESS_INDEX_NAME}/list.vue`);
export const ADDRESS_ADD = () =>
    import(`../components/${ADDRESS_INDEX_NAME}/add.vue`);

export const COURSE_MODULE_INDEX = () =>
    import(`../components/${COURSE_MODULE_INDEX_NAME}/index.vue`);
export const COURSE_MODULE_LIST = () =>
    import(`../components/${COURSE_MODULE_INDEX_NAME}/list.vue`);
export const COURSE_MODULE_ADD = () =>
    import(`../components/${COURSE_MODULE_INDEX_NAME}/add.vue`);

export const ENROLMENT_INDEX = () =>
    import(`../components/${ENROLMENT_INDEX_NAME}/index.vue`);
export const ENROLMENT_LIST = () =>
    import(`../components/${ENROLMENT_INDEX_NAME}/list.vue`);
export const ENROLMENT_ADD = () =>
    import(`../components/${ENROLMENT_INDEX_NAME}/add.vue`);

export const STUDENT_INDEX = () =>
    import(`../components/${STUDENT_INDEX_NAME}/index.vue`);
export const STUDENT_LIST = () =>
    import(`../components/${STUDENT_INDEX_NAME}/list.vue`);
export const STUDENT_ADD = () =>
    import(`../components/${STUDENT_INDEX_NAME}/add.vue`);

// Demo design pattern:
export const DEMO = () => import(`../components/${DEMO_NAME}/demo.vue`);
