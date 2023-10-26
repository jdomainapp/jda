import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentClassComponent } from './student-class/student-class.component';
import { AddressComponent } from './address/address.component';
import { CourseModuleComponent } from './course-module/course-module.component';
import { EnrolmentComponent } from './enrolment/enrolment.component';
import { StudentComponent } from './student/student.component';

const routes: Routes = [
    {path:'student-classes' , component:StudentClassComponent, pathMatch: 'full'},
    {path:'addresses' , component:AddressComponent, pathMatch: 'full'},
    {path:'course-modules' , component:CourseModuleComponent, pathMatch: 'full'},
    {path:'enrolments' , component:EnrolmentComponent, pathMatch: 'full'},
    {path:'students' , component:StudentComponent, pathMatch: 'full'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
