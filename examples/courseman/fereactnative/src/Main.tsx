import * as React from 'react';
import JDARouter from './base/views/jda_router/JDARouter';
import {Modules} from './data_types/enums/Modules';
import {StudentClassModule} from './modules/student_class/Index';
import {AddressModule} from './modules/address/Index';
import {CourseModuleModule} from './modules/course_module/Index';
import {EnrolmentModule} from './modules/enrolment/Index';
import {StudentModule} from './modules/student/Index';

export default class MainScreen extends React.Component {
  public render() {
    return (
      <JDARouter
        homeScreenOptions={{
          title: 'Courseman',
        }}
        routeConfigs={[
          {
            component: StudentClassModule,
            name: Modules.StudentClass,
            title: 'Student classes',
          },
          {
            component: AddressModule,
            name: Modules.Address,
            title: 'Addresses',
          },
          {
            component: CourseModuleModule,
            name: Modules.CourseModule,
            title: 'Course modules',
          },
          {
            component: EnrolmentModule,
            name: Modules.Enrolment,
            title: 'Enrolments',
          },
          {
            component: StudentModule,
            name: Modules.Student,
            title: 'Students',
          },
        ]}
      />
    );
  }
}
