import { Directive, ElementRef, Input, Renderer2, ViewContainerRef } from '@angular/core';
import { PatternService } from './pattern.service';

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
        // TODO:
    }
}