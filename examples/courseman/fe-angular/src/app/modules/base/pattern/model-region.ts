import { Directive, ElementRef, Host, Input, Optional, Renderer2, ViewContainerRef } from '@angular/core';
import { PatternService } from './pattern.service';
import { BaseFormComponent } from '../components/base-form/base-form.component';

@Directive({
    selector: '[modelRegion]',
})
export class ModelRegionDirective {
    @Input() modelRegion = '';

    constructor(
        @Host() public element: ElementRef,
        private patternService: PatternService,
    ) { }

    ngAfterViewInit() {
        this.patternService.renderModelRegion(this);
    }
}