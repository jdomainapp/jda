import {IJDAListConfig} from '../../base/creators/createListComponents';
import {Enrolment} from '../../data_types/Enrolment';
export const EnrolmentListConfig: IJDAListConfig<Enrolment> = {
  listItemProps: {
    icon: 'person-outline',
    title: (enrolment) =>
      ` ${enrolment.id} | ${enrolment.internalMark} | ${enrolment.examMark} | ${enrolment.finalGrade} | ${enrolment.finalMark} |`,
  },
  listProps: {},
};
