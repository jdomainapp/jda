import { Component, Host, Optional, SkipSelf } from '@angular/core';

@Component({
    selector: 'AccordionComponent',
    templateUrl: './accordion.component.html',
})
export class AccordionComponent {
    items: any[] = []; 
    show: boolean = true;
    scrollSpy: any;
    
    constructor(@SkipSelf() @Host() @Optional() public parent?: AccordionComponent) {
       
    }

    ngAfterViewInit(): void {
        
    }

    onToggle() {
        this.show = !this.show;
    }
}