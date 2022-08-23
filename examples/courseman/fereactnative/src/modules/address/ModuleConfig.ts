import {IJDAModuleConfig} from '../../base/controllers/jda_module_controller/withModuleController';
import {Modules} from '../../data_types/enums/Modules';
import {Address} from '../../data_types/Address';

export const AddressModuleConfig: IJDAModuleConfig<Address> = {
  primaryKey: 'id',
  route: Modules.Address,
  apiResource: 'addresses',
  moduleName: 'Addresses',
  fieldLabel: {
    id: 'ID',
    name: 'City name',
    student: 'Student',
  },
  quickRender: (address) =>
    address ? ` ${address.id} | ${address.name} |` : '',
  apiConfig: {
    toPOST: (address) => {
      return {
        ...address,
        studentId: address.student?.id,
      };
    },
  },
};
