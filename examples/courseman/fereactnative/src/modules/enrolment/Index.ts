import {Enrolment} from '../../data_types/Enrolment';
import {EnrolmentListConfig} from './ListConfig';
import {EnrolmentModuleConfig} from './ModuleConfig';

import {createModuleComponents} from '../../base/creators/createModuleComponents';
import {EnrolmentFormConfig} from './FormConfig';
export const {
  Module: EnrolmentModule,
  List: EnrolmentList,
  ListItem: EnrolmentListItem,
  Form: EnrolmentForm,
} = createModuleComponents<Enrolment>(
  EnrolmentModuleConfig,
  EnrolmentListConfig,
  EnrolmentFormConfig,
);
