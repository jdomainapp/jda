import { Router } from '@angular/router';
import { Component, Host, Input, Optional, SkipSelf } from '@angular/core';

@Component({
    selector: 'AccordionComponent',
    templateUrl: './accordion.component.html',
})
export class AccordionComponent {
    @Input() items: any[] = []; 
    filteredItems: any[] = [];

    searchKeyword = "";
    prefix = '';

    constructor(
        private router: Router,
        @SkipSelf() @Host() @Optional() public parent?: AccordionComponent
    ) {
        
    }

    ngOnInit() {
        this.search();
    }

    ngAfterViewInit(): void {
        
    }

    search() {
        this.filteredItems = this.items;
        
        if (this.searchKeyword) {
            const keyword = this.searchKeyword.toLowerCase();
            this.filteredItems = this.items.filter(item => item.name.toLowerCase().includes(keyword));
        }
    }
    
}