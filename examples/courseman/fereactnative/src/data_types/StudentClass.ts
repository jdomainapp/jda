import {Student} from './Student';

export interface StudentClass {
  id?: number;
  name: string;
  students?: Student[];
}
