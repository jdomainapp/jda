import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddressComponent } from './address/address.component';
import { StudentComponent } from './student/student.component';
import { StudentClassComponent } from './student-class/student-class.component';
import { CourseModuleComponent } from './course-module/course-module.component';
import { EnrolmentComponent } from './enrolment/enrolment.component';

const routes: Routes = [
    {path:'addresses' , component:AddressComponent, pathMatch: 'full'},
    {path:'students' , component:StudentComponent, pathMatch: 'full'},
    {path:'student-classes' , component:StudentClassComponent, pathMatch: 'full'},
    {path:'course-modules' , component:CourseModuleComponent, pathMatch: 'full'},
    {path:'enrolments' , component:EnrolmentComponent, pathMatch: 'full'},  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
