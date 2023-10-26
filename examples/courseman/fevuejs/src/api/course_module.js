import client from './base/client.js';

const END_POINT = '/course-modules';

const getAllCourseModules = () => client.get(END_POINT);
const getCourseModule = (course_module_id) => client.get(END_POINT + "/" + course_module_id);
const deleteCourseModule = (course_module_id) => client.delete(END_POINT + "/" + course_module_id);
const addCourseModule = (data) => client.post(END_POINT, data);
const updateCourseModule = (course_module_id, data) => client.patch(END_POINT + "/" + course_module_id, data);
const getInnerListByOuterId = (parentID,parentEndPoint) => client.get(parentEndPoint + "/" + parentID +END_POINT);

export {
    getAllCourseModules,
    getCourseModule,
    deleteCourseModule,
    addCourseModule,
    updateCourseModule,
    getInnerListByOuterId
}