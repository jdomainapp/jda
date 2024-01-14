import { Component, EventEmitter, Input, Output } from "@angular/core";

@Component({
    selector: 'AutoSearchComponent',
    templateUrl: './autosearch.component.html'
})
export class AutoSearchComponent {
    @Input() items: any[] = [];
    
    @Input() keyword: string = '';
    @Output() keywordChange = new EventEmitter<string>();
    
    onChange() {
        this.keywordChange.emit(this.keyword);
    }
}