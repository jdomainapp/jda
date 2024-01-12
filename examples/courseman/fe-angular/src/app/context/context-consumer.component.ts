import { Component, ContentChild, EmbeddedViewRef, Input, OnDestroy, OnInit, Optional, SimpleChanges, TemplateRef, ViewContainerRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { ProviderComponent } from './context-provider.component';
import { ContextComponent } from './context.component';
import { assertNotNullOrUndefined, assertStringIsNotEmpty } from './utils';


@Component({
    selector: 'consumer',
    template: '<ng-content></ng-content>',
})
export class ConsumerComponent implements OnInit, OnDestroy {
    @Input() name!: string;
    @ContentChild(TemplateRef, { static: true }) templateRef!: TemplateRef<any>;
    private embeddedView?: EmbeddedViewRef<any>;
    
    private providerValueChangesSubscription?: Subscription;
    private consumerContext!: ContextComponent;
    private consumerProvider?: ProviderComponent;
    
    constructor(
        private viewContainerRef: ViewContainerRef,
        @Optional() private context: ContextComponent,
        @Optional() private provider: ProviderComponent | null,
        ) { }
        
        ngOnInit(): void {
            assertStringIsNotEmpty(this.name, 'Consumer context name');
            
            if (this.templateRef === undefined) {
                throw new Error(`Cannot find <ng-template>, you may forget to put the content in <ng-template>.
                If you do not want to put the content in context then no point in using it.`);
            }
            if (this.context === null) {
                throw new Error('Non of consumer ancestors is a context component, ensure you are using consumer as context descendants.');
            }
            
            this.consumerContext = this.context.getContext(this.name)!;
            this.consumerProvider = this.provider?.getProvider?.(this.name);
            assertNotNullOrUndefined(this.consumerContext, `Consumer context ${this.name}`);
            
            if (this.consumerProvider) {
                this.providerValueChangesSubscription = this.consumerProvider
                .valueChanges()
                .subscribe((providerValue) => {
                    this.render(providerValue);
                });
            } else {
                this.render(this.consumerContext.defaultValue);
            }
        }
        
        ngOnChanges(changes: SimpleChanges): void {
            const nameChange = changes['name'];
            if (nameChange && !nameChange.isFirstChange()) {
                const { currentValue, previousValue } = nameChange;
                throw new Error(`Context name can be initialized only once.\n Original name ${previousValue}\n New name ${currentValue}`);
            }
        }
        
        private buildTemplate(initialValue: any) {
            this.embeddedView = this.viewContainerRef.createEmbeddedView(this.templateRef, {
                $implicit: initialValue
            });
        }
        
        private updateTemplate(newValue: string) {
            this.embeddedView!.context = {
                $implicit: newValue
            };
            this.embeddedView?.markForCheck();
        }
        
        private render(value: any) {
            if (this.embeddedView) {
                this.updateTemplate(value);
            } else {
                this.buildTemplate(value);
            }
        }
        
        ngOnDestroy(): void {
            this.providerValueChangesSubscription?.unsubscribe();
        }
        
    }
    