import client from './base/client.js';

const END_POINT = '/students';

const getAllStudents = () => client.get(END_POINT);
const getStudent = (student_id) => client.get(END_POINT + "/" + student_id);
const deleteStudent = (student_id) => client.delete(END_POINT + "/" + student_id);
const addStudent = (data) => client.post(END_POINT, data);
const updateStudent = (student_id, data) => client.patch(END_POINT + "/" + student_id, data);
const getInnerListByOuterId = (parentID,parentEndPoint) => client.get(parentEndPoint + "/" + parentID +END_POINT);

export {
    getAllStudents,
    getStudent,
    deleteStudent,
    addStudent,
    updateStudent,
    getInnerListByOuterId
}