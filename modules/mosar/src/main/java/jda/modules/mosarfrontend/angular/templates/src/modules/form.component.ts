import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: '@slot{{selector}}',
  templateUrl: '@slot{{html-path}}', //./address-form.component.html
})
export class @slot{{componentName}} extends BaseFormComponent implements FormComponent{
    // item: any = {};  
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

    @Input() studentId: string = '';

    ngAfterViewInit() {
      if (this.studentId) {
        this.getStudent(this.studentId);
        console.log('Student: ', this.item.student);
      }
    } 

    override apiName = '@slot{{api}}';

    getStudent(event: any) {
      console.log(event);
      this.service.init('students');
      this.getItem(event, 'student');
      this.service.init(this.apiName);
    }    
  }
