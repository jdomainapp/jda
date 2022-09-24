import client from './base/client.js';

const END_POINT = '/student-classes';

const getAllStudentClasses = () => client.get(END_POINT);
const getStudentClass = (student_class_id) => client.get(END_POINT + "/" + student_class_id);
const getInnerListByOuterId = (student_class_id) => client.get(END_POINT + "/" + student_class_id + "/students");
const deleteStudentClass = (student_class_id) => client.delete(END_POINT + "/" + student_class_id);
const addStudentClass = (data) => client.post(END_POINT, data);
const updateStudentClass = (student_class_id, data) => client.patch(END_POINT + "/" + student_class_id, data);

export {
    getAllStudentClasses,
    getStudentClass,
    deleteStudentClass,
    addStudentClass,
    updateStudentClass,
    getInnerListByOuterId
}