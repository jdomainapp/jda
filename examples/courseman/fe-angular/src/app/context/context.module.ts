import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ConsumerComponent } from './context-consumer.component';
import { ProviderComponent } from './context-provider.component';
import { ContextComponent } from './context.component';

@NgModule({
    declarations: [ContextComponent, ProviderComponent, ConsumerComponent],
    exports: [ContextComponent, ProviderComponent, ConsumerComponent],
    imports: [CommonModule]
})
export class ContextModule { }
