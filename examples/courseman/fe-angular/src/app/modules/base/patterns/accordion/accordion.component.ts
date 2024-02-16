import { Router } from '@angular/router';
import { Component, Host, Input, Optional, SkipSelf } from '@angular/core';
import { AccordionService } from './accordion.service';

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
        private accordionService: AccordionService,
        @SkipSelf() @Host() @Optional() public parent?: AccordionComponent
    ) {
    }

    ngOnInit() {
        this.search();
    }

    search() {
        this.accordionService.searchKeyword = this.searchKeyword;

        this.filteredItems = this.items;
        
        if (this.searchKeyword) {
            const keyword = this.searchKeyword.toLowerCase();
            // this.filteredItems = this.items.filter(item => item.name.toLowerCase().includes(keyword));
            this.filteredItems = this.filter(this.filteredItems, keyword);
        }
    }

    filter(items: any[], keyword: string) {
        let results: any[] = [];

        for (const item of items) {
            if (item.subItem && item.subItem.length > 0) {
                let _results = this.filter(item.subItem, keyword);

                if (_results.length > 0) {
                    let _item = JSON.parse(JSON.stringify(item));
                    _item.subItem = _results;
                    results.push(_item);
                }
            } else {
                if (item.name.toLowerCase().includes(keyword)) {
                    results.push(item);
                }
            }
        }

        return results;
    }
}