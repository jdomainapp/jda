import client from './base/client.js';

const END_POINT = '/addresses';

const getAllAddresses = () => client.get(END_POINT);
const getAddress = (address_id) => client.get(END_POINT + "/" + address_id);
const deleteAddress = (address_id) => client.delete(END_POINT + "/" + address_id);
const addAddress = (data) => client.post(END_POINT, data);
const updateAddress = (address_id, data) => client.patch(END_POINT + "/" + address_id, data);

export {
    getAllAddresses,
    getAddress,
    deleteAddress,
    addAddress,
    updateAddress
}