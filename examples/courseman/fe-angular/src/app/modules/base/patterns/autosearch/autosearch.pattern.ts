import { ComponentRef } from "@angular/core";
import { Subject, takeUntil } from "rxjs";
import { ViewRegionComponent } from 'src/app/modules/base/pattern/view-region';
import { Pattern } from "../../pattern/pattern";
import { AutoSearchComponent } from "./autosearch.component";

export class AutoSearchPattern extends Pattern {
    
    items: any[] = [];

    componentRef!: ComponentRef<AutoSearchComponent>;
    closed$ = new Subject<any>();
    
    constructor(items: any[]) {
        super();

        this.items = items;
    }

    override render(region: ViewRegionComponent): void {
        region.container.clear();
        this.componentRef = region.container.createComponent(AutoSearchComponent);
        this.componentRef.instance.items = this.items;

        this.componentRef.changeDetectorRef.detectChanges();

        this.componentRef.instance.keywordChange
            .pipe(takeUntil(this.closed$))
            .subscribe((event: any) => this.onAction('select', event));
    }

    override onDataChange(data: any = {}): void {
        this.items = data.items;

        this.componentRef.instance.items = this.items;
    }
}