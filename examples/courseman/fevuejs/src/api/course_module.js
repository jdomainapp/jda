import client from './base/client.js';

const END_POINT = '/course-modules';

const getAllCourseModules = () => client.get(END_POINT);
const getCourseModule = (course_id) => client.get(END_POINT + "/" + course_id);
const deleteCourseModule = (course_id) => client.delete(END_POINT + "/" + course_id);
const addCourseModule = (data) => client.post(END_POINT, data);
const updateCourseModule = (course_id, data) => client.patch(END_POINT + "/" + course_id, data);

export {
    getAllCourseModules,
    getCourseModule,
    deleteCourseModule,
    addCourseModule,
    updateCourseModule
}