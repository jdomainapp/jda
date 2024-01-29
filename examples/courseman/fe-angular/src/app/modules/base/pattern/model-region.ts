import { Directive, ElementRef, Host, Input, Renderer2, ViewContainerRef } from '@angular/core';
import { PatternService } from './pattern.service';

@Directive({
    selector: '[modelRegion]',
})
export class ModelRegionDirective {
    @Input() modelRegion = '';

    constructor(
        @Host() public element: ElementRef,
        private patternService: PatternService
    ) { }

    ngAfterViewInit() {
        this.patternService.renderModelRegion(this);
    }
}