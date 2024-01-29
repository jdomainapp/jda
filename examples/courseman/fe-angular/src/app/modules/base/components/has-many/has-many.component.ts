import { Component, ContentChild, Input, TemplateRef } from '@angular/core';
import { BaseComponent } from '../base/base.component';
import { Scroll } from '@angular/router';

@Component({
  selector: 'app-has-many',
  templateUrl: './has-many.component.html',
})
export class HasManyComponent extends BaseComponent {
  @Input() label: string = '';
  @Input() id: string = '';
  @ContentChild('subform') subformTemplate!: TemplateRef<any>;

  formVisible: boolean = false;

  toggleForm(): void {
    this.formVisible = !this.formVisible;
  }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (event instanceof Scroll) {
        if (event.anchor?.startsWith(this.id)) {
          this.formVisible = true;
        } 
      }
    });
  }

}