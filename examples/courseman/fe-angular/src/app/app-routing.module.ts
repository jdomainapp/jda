import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentClassComponent } from './student-class/components/student-class/student-class.component';
import { StudentComponent } from './student/components/student/student.component';
import { AddressComponent } from './address/components/address/address.component';
import { CourseModuleComponent } from './course-module/components/course-module/course-module.component';
import { EnrolmentComponent } from './enrolment/components/enrolment/enrolment.component';

const routes: Routes = [
  { path: 'student-classes', component: StudentClassComponent },
  { path: 'students', component: StudentComponent },
  { path: 'addresses', component: AddressComponent },
  { path: 'course-modules', component: CourseModuleComponent },
  { path: 'enrolments', component: EnrolmentComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule] 
})
export class AppRoutingModule { }
