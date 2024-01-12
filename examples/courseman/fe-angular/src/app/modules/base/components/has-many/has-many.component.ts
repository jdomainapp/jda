import { Component, ContentChild, Input, TemplateRef } from '@angular/core';
import { BaseComponent } from '../base/base.component';

@Component({
  selector: 'app-has-many',
  templateUrl: './has-many.component.html',
})
export class HasManyComponent extends BaseComponent {
  @Input() label: string = '';
  @ContentChild('subform') subformTemplate!: TemplateRef<any>;

  formVisible: boolean = false;

  toggleForm(): void {
    this.formVisible = !this.formVisible;
  }

}