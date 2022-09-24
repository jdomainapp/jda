import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: 'app-course-module-form',
  templateUrl: './course-module-form.component.html',
})
export class CourseModuleFormComponent extends BaseFormComponent implements FormComponent{
    // item: any = {};  
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

    override apiName = 'course-modules';
  }
