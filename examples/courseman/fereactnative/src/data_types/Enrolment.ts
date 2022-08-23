import {Student} from './Student';
import {CourseModule} from './CourseModule';

export interface Enrolment {
  id: number;
  student: Omit<Student, 'enrolments'>;
  courseModule: CourseModule;
  internalMark?: number;
  examMark?: number;
  finalGrade?: string;
  finalMark?: number;
}
