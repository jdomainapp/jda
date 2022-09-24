import {Gender} from './enums/Gender';

import {Address} from './Address';
import {StudentClass} from './StudentClass';
import {Enrolment} from './Enrolment';

export interface Student {
  id: string;
  name: string;
  gender: Gender;
  dob: Date;
  address?: Omit<Address, 'student'>;
  email: string;
  studentClass?: Omit<StudentClass, 'students'>;
  enrolments: Enrolment[];
}
