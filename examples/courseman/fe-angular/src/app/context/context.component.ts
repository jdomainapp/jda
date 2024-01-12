import {
    Component,
    Input,
    OnChanges,
    OnInit,
    Optional,
    SimpleChanges,
    SkipSelf
} from '@angular/core';
import { assertStringIsNotEmpty } from './utils';

@Component({
    selector: 'context',
    template: '<ng-content></ng-content>'
})
export class ContextComponent implements OnInit, OnChanges {
    @Input() defaultValue?: any;
    @Input() name!: string;

    constructor(
        @Optional() @SkipSelf() public parentContext: ContextComponent | null
    ) { }

    ngOnInit(): void {
        assertStringIsNotEmpty(this.name, 'Context name');
        this.ensureContextUniqueness(this.name);
    }

    ngOnChanges(changes: SimpleChanges): void {
        const nameChange = changes['name'];
        if (nameChange && !nameChange.isFirstChange()) {
            const { currentValue, previousValue } = nameChange;
            throw new Error(
                `Context name can be initialized only once.\n Original name ${previousValue}\n New name ${currentValue}`
            );
        }
    }

    public ensureContextUniqueness(contextName: string) {
        let context: ContextComponent | null = this.parentContext;
        while (context !== null) {
            if (context.name === contextName) {
                throw new Error(`Context ${this.name} already exist.`);
            }
            context = context.parentContext;
        }
    }

    public getContext(contextName: string) {
        let context: ContextComponent | null = this;
        while (context !== null) {
            if (context.name === contextName) {
                return context;
            }
            context = context.parentContext;
        }
        return undefined;
    }
}
