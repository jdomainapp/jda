import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ConsumerComponent } from './pattern-consumer';
import { PatternProviderComponent } from './pattern.provider';
import { PatternComponent } from './pattern.component';
import { ModelRegionDirective } from './model-region';

@NgModule({
    declarations: [PatternComponent, PatternProviderComponent, ConsumerComponent, ModelRegionDirective],
    exports: [PatternComponent, PatternProviderComponent, ConsumerComponent, ModelRegionDirective],
    imports: [CommonModule]
})
export class PatternModule { }
