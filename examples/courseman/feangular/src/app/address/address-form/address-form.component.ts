import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: 'app-address-form',
  templateUrl: './address-form.component.html',
})
export class AddressFormComponent extends BaseFormComponent implements FormComponent{
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

    @Input() studentId: string = '';

    override apiName = 'addresses';


    ngAfterViewInit() {
      if (this.studentId) {
        this.getStudent(this.studentId);
      }
    }

    getStudent(event: any) {
      this.getItem(event, 'student', 'students');
    }

  }
