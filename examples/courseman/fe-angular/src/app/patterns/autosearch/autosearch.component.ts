import { Component, Input } from "@angular/core";
import { PatternComponent } from "src/app/pattern/pattern.component";

@Component({
    selector: 'AutoSearchComponent',
    templateUrl: './autosearch.component.html'
})
export class AutoSearchComponent extends PatternComponent {
    @Input() items: any[] = [];
    
    searchKeyword: string = '';

    
}