import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, Type, ViewChild, ViewContainerRef } from '@angular/core';
import { FormComponent } from 'src/app/common/form.component';
import { CompDirective } from 'src/app/common/comp.directive';
import { BaseFormComponent } from './base-form.component';

@Component({
  selector: 'app-base-form-directive',
  template: `<div>      
              <ng-template compHost></ng-template></div>`,
})
export class BaseFormDirectiveComponent implements OnInit, OnChanges {
  @Input() form_type: Type<any> = BaseFormComponent;
  @Input() search_id: any = '';
  @Input() deleteFlag: boolean = false;
  @ViewChild(CompDirective, {static: true}) compHost!: CompDirective;
  comp: any;
  show_alert: boolean = true;

  constructor() {
  }

  ngOnInit(): void {
    // this.service_init();
    this.loadComponent();
  }

  ngAfterViewInit() {
    if (this.search_id) {
      this.comp.search(this.search_id);
    }
  } 

  ngOnChanges(changes: SimpleChanges): void {
    // console.log(changes['search_id']);
    // console.log(this.search_id);
    
      for (let propName in changes) {
        if (propName == 'search_id' && this.search_id != '') {
          if (!changes['search_id'].isFirstChange()) {
            this.comp.search(this.search_id);
          }
        } else {
          if (propName == 'deleteFlag' && this.deleteFlag == true) {
            if (!changes['deleteFlag'].isFirstChange()) {
              this.comp.delete(this.search_id);
            }
          }
        }
      }
  }
  
  loadComponent() {
    const viewContainerRef = this.compHost.viewContainerRef;
    viewContainerRef.clear();

    const componentRef = viewContainerRef.createComponent<FormComponent>(this.form_type);
    componentRef.instance._item = {};
    this.comp = componentRef.instance;
  }

  deleteClick() {
    console.log('delete');
    // console.log('COMP', this.comp.status);
    // this.comp.delete(this.search_id);
  }
}
