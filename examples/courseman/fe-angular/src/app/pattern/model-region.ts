import { Directive, ElementRef, Input, Renderer2, ViewContainerRef } from '@angular/core';
import { PatternService } from './pattern.consumer';

@Directive({
    selector: '[modelRegion]',

})
export class ModelRegionDirective {
    @Input() modelRegion = '';

    constructor(
        private container: ViewContainerRef,
        private patternService: PatternService
    ) { }

    ngOnInit() {
        // foreach consumers with region name
        // consumer.render(region)
        
        // this.patternService.onRenderRegion(this.region);
        
        
        // [typeahead] = "items" typeaheadOptionField = "name"[isAnimated] = "true"
    }
}