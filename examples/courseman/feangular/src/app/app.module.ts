import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule} from './app-routing.module';
import { AppComponent } from './app.component';
import { AddressComponent } from './address/address.component';
import { StudentComponent } from './student/student.component';
import { BaseComponent } from './base/base.component';
import { BaseFormComponent } from './base/base-form/base-form.component';
import { BaseListComponent } from './base/base-list/base-list.component';
import { BaseFormDirectiveComponent } from './base/base-form/base-form-directive.component';
import { AddressFormComponent } from './address/address-form/address-form.component';
import { StudentFormComponent } from './student/student-form/student-form.component';
import { CompDirective } from './common/comp.directive';
import { BaseService } from './base/base.service';
import { StudentClassComponent } from './student-class/student-class.component';
import { StudentClassFormComponent } from './student-class/student-class-form/student-class-form.component';
import { CourseModuleComponent } from './course-module/course-module.component';
import { CourseModuleFormComponent } from './course-module/course-module-form/course-module-form.component';
import { EnrolmentComponent } from './enrolment/enrolment.component';
import { EnrolmentFormComponent } from './enrolment/enrolment-form/enrolment-form.component';
import { BaseCommonComponent } from './base/base-common/base-common.component';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NotificationService } from './common/notification.service';

@NgModule({
  declarations: [
    AppComponent,
    AddressComponent,
    StudentComponent,
    StudentClassComponent,
    BaseComponent,
    BaseFormComponent,
    BaseListComponent,
    BaseFormDirectiveComponent,
    CompDirective,
    AddressFormComponent,
    StudentFormComponent,
    StudentClassComponent,
    StudentClassFormComponent,
    CourseModuleComponent,
    CourseModuleFormComponent,
    EnrolmentComponent,
    EnrolmentFormComponent,
    BaseCommonComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule ,
    AppRoutingModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule
  ],
  providers: [BaseService, NotificationService],
  bootstrap: [AppComponent]
})
export class AppModule { }
