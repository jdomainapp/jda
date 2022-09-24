import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: 'app-student-form',
  templateUrl: './student-form.component.html',
})
export class StudentFormComponent extends BaseFormComponent implements FormComponent{
    // item: any = {};  
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

    @Input() addressId: string = '';
        
    override apiName = 'students';
    address_id: any = '';
    enroll_flag: boolean = false;

    ngAfterViewInit() {
      if (this.addressId) {
        this.getAddress(this.addressId);
        console.log('Address: ', this.item.address);
      }
    } 

    getAddress(event: any) {
      // console.log('before:', this.item);
      this.service.init('addresses');
      this.getItem(event, 'address');
      // console.log('after', this.item); 
      this.service.init(this.apiName);    
    }
    
    changeEnrollFlag() {
      this.enroll_flag = !this.enroll_flag;
    }
  }
