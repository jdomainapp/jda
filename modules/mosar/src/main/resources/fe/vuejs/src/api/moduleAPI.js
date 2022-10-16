import client from './base/client.js';

const END_POINT = '/@slot{{moduleJnames}}';

const getAll@slot{{ModuleNames}} = () => client.get(END_POINT);
const get@slot{{ModuleName}} = (@slot{{module_name}}_id) => client.get(END_POINT + "/" + @slot{{module_name}}_id);
const delete@slot{{ModuleName}} = (@slot{{module_name}}_id) => client.delete(END_POINT + "/" + @slot{{module_name}}_id);
const add@slot{{ModuleName}} = (data) => client.post(END_POINT, data);
const update@slot{{ModuleName}} = (@slot{{module_name}}_id, data) => client.patch(END_POINT + "/" + @slot{{module_name}}_id, data);
const getInnerListByOuterId = (parentID,parentEndPoint) => client.get(parentEndPoint + "/" + parentID +END_POINT);

export {
    getAll@slot{{ModuleNames}},
    get@slot{{ModuleName}},
    delete@slot{{ModuleName}},
    add@slot{{ModuleName}},
    update@slot{{ModuleName}},
    getInnerListByOuterId
}