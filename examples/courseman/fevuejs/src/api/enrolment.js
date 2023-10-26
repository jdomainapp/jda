import client from './base/client.js';

const END_POINT = '/enrolments';

const getAllEnrolments = () => client.get(END_POINT);
const getEnrolment = (enrolment_id) => client.get(END_POINT + "/" + enrolment_id);
const deleteEnrolment = (enrolment_id) => client.delete(END_POINT + "/" + enrolment_id);
const addEnrolment = (data) => client.post(END_POINT, data);
const updateEnrolment = (enrolment_id, data) => client.patch(END_POINT + "/" + enrolment_id, data);
const getInnerListByOuterId = (parentID,parentEndPoint) => client.get(parentEndPoint + "/" + parentID +END_POINT);

export {
    getAllEnrolments,
    getEnrolment,
    deleteEnrolment,
    addEnrolment,
    updateEnrolment,
    getInnerListByOuterId
}