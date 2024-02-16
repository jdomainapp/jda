import {
    Component,
    ContentChild,
    Input,
    TemplateRef,
    ViewContainerRef
} from '@angular/core';
import { PatternService } from './pattern.service';

@Component({
    selector: 'ViewRegion',
    template: '<ng-content></ng-content>'
})
export class ViewRegionComponent {
    @Input() name!: string;
    
    @ContentChild(TemplateRef) templateRef!: TemplateRef<any>;

    constructor(
        public container: ViewContainerRef,
        private patternService: PatternService,
    ) { }
    
    ngAfterViewInit() {
        this.patternService.render(this);
    }
}
