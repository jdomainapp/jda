import {
    Component,
    Input,
    OnDestroy,
    OnInit,
    Optional,
    SimpleChanges,
    SkipSelf,
} from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { ContextComponent } from './context.component';
import { assertNotNullOrUndefined, assertStringIsNotEmpty } from './utils';

@Component({
    selector: 'provider',
    template: '<ng-content></ng-content>',
})
export class ProviderComponent implements OnInit, OnDestroy {
    @Input() name!: string;
    @Input() value?: any;
    private providerContext!: ContextComponent;
    private valueState = new ReplaySubject<any>(1);

    constructor(
        @Optional() @SkipSelf() public parentProvider: ProviderComponent | null,
        @Optional() private context: ContextComponent | null
    ) { }

    ngOnInit(): void {
        assertStringIsNotEmpty(this.name, 'Provider context name');

        if (this.value === undefined) {
            throw new Error(`Provider without value is worthless.`);
        }
        if (this.context === null) {
            throw new Error(
                'Non of provider ancestors is a context component, ensure you are using provider as context descendants.'
            );
        }
        this.providerContext = this.context.getContext(this.name)!;
        assertNotNullOrUndefined(
            this.providerContext,
            `Provider context ${this.name}`
        );
    }

    ngOnChanges(changes: SimpleChanges): void {
        const nameChange = changes['name'];
        if (nameChange && !nameChange.isFirstChange()) {
            const { currentValue, previousValue } = nameChange;
            throw new Error(
                `Context name can be initialized only once.\n Original name ${previousValue}\n New name ${currentValue}`
            );
        }

        const valueChange = changes['value'];
        if (valueChange) {
            this.broadcastValueChanges(valueChange.currentValue);
        }
    }

    private broadcastValueChanges(newValue: any) {
        this.valueState.next(newValue);
    }

    public valueChanges() {
        return this.valueState.asObservable();
    }

    public getProvider(contextName: string) {
        let provider: ProviderComponent | null = this;
        while (provider !== null) {
            if (provider.name === contextName) {
                return provider;
            }
            provider = provider.parentProvider;
        }
        return undefined;
    }

    ngOnDestroy(): void {
        this.valueState.complete();
    }
}
