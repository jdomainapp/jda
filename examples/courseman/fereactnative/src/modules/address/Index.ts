import {Address} from '../../data_types/Address';
import {AddressListConfig} from './ListConfig';
import {AddressModuleConfig} from './ModuleConfig';

import {createModuleComponents} from '../../base/creators/createModuleComponents';
import {AddressFormConfig} from './FormConfig';
export const {
  Module: AddressModule,
  List: AddressList,
  ListItem: AddressListItem,
  Form: AddressForm,
} = createModuleComponents<Address>(
  AddressModuleConfig,
  AddressListConfig,
  AddressFormConfig,
);
