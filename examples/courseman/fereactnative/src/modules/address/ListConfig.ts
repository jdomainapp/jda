import {IJDAListConfig} from '../../base/creators/createListComponents';
import {Address} from '../../data_types/Address';
export const AddressListConfig: IJDAListConfig<Address> = {
  listItemProps: {
    icon: 'person-outline',
    title: (address) => ` ${address.id} | ${address.name} |`,
  },
  listProps: {},
};
