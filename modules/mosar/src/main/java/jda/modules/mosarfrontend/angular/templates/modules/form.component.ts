import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: '{{ view.form.selector }}',
  templateUrl: '{{ view.form.html.path }}', //./address-form.component.html
})
export class {{  view.name.form }} extends BaseFormComponent implements FormComponent{
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
    // @Input() override show_component: boolean;
    // set _show_component(_show_component: boolean) {
    //   this.show_component = _show_component;
    // }
    // get _show_component(): boolean { return this.show_component};
    
    override apiName = '{{ view.api }}';

    getStudent(event: any) {
      console.log(event);
      this.service.init('students');
      this.getItem(event, 'student');
      this.service.init(this.apiName);
    }    
  }
