import {CourseModule} from './CourseModule';

export interface ElectiveModule extends CourseModule {
  deptName: string;
}
