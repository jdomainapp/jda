import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BaseModule } from './modules/base/base.module';
import { BaseService } from './modules/base/services/base.service';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbRatingModule } from '@ng-bootstrap/ng-bootstrap';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { RatingModule } from 'ngx-bootstrap/rating';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { AddressFormComponent } from './address/components/address-form/address-form.component';
import { AddressListComponent } from './address/components/address-list/address-list.component';
import { AddressComponent } from './address/components/address/address.component';
import { AccordionComponent } from './components/accordion/accordion.component';
import { FragmentComponent } from './components/accordion/fragment.component';
import { CourseModuleFormComponent } from './course-module/components/course-module-form/course-module-form.component';
import { CourseModuleListComponent } from './course-module/components/course-module-list/course-module-list.component';
import { CourseModuleComponent } from './course-module/components/course-module/course-module.component';
import { EnrolmentFormComponent } from './enrolment/components/enrolment-form/enrolment-form.component';
import { EnrolmentListComponent } from './enrolment/components/enrolment-list/enrolment-list.component';
import { EnrolmentComponent } from './enrolment/components/enrolment/enrolment.component';
import { StudentClassFormComponent } from './student-class/components/student-class-form/student-class-form.component';
import { StudentClassListComponent } from './student-class/components/student-class-list/student-class-list.component';
import { StudentClassComponent } from './student-class/components/student-class/student-class.component';
import { StudentFormComponent } from './student/components/student-form/student-form.component';
import { StudentListComponent } from './student/components/student-list/student-list.component';
import { StudentComponent } from './student/components/student/student.component';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { NgxSliderModule } from 'ngx-slider-v2';
import { AutoSearchComponent } from './patterns/autosearch/autosearch.component';

@NgModule({
  declarations: [
    AppComponent,

    AddressComponent,
    AddressListComponent,
    AddressFormComponent,

    StudentComponent,
    StudentListComponent,
    StudentFormComponent,

    StudentClassComponent,
    StudentClassListComponent,
    StudentClassFormComponent,

    CourseModuleComponent,
    CourseModuleListComponent,
    CourseModuleFormComponent,

    EnrolmentComponent,
    EnrolmentListComponent,
    EnrolmentFormComponent,

    AccordionComponent,
    FragmentComponent,

    AutoSearchComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,

    BaseModule,
    FormsModule,
    ReactiveFormsModule,

    // PatternModule,
      BrowserAnimationsModule,
      TypeaheadModule.forRoot(),
      RatingModule.forRoot(),
    // NgbModule
    NgbRatingModule,
    BsDatepickerModule.forRoot(),
    AccordionModule.forRoot(), 
    NgxSliderModule
  ],
  providers: [BaseService],
  bootstrap: [AppComponent]
})
export class AppModule { }
