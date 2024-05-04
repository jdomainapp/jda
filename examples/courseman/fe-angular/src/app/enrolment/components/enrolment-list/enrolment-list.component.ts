import { Component } from '@angular/core';
import { BaseListComponent } from 'src/app/modules/base/components/base-list/base-list.component';

@Component({
  selector: 'app-enrolment-list',
  templateUrl: './../../../modules/base/components/base-list/base-list.component.html',
})
export class EnrolmentListComponent extends BaseListComponent {
  override apiName = "enrolments";
  
  override columns = [
    { field: 'id', title: 'ID' },
    { field: 'student', title: 'Student' },
    { field: 'courseModule', title: 'Course Module' },
    { field: 'internalMark', title: 'Internal Mark' },
    { field: 'examMark', title: 'Exam Mark' },
    { field: 'finalMark', title: 'Final Mark' },
    { field: 'finalGrade', title: 'Final Grade' },
    { field: 'startDate', title: 'Start Date' },
    { field: 'endDate', title: 'End Date' },
  ];

}
