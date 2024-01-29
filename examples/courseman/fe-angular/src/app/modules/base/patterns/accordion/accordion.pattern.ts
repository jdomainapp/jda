import { ComponentRef } from "@angular/core";
import { Subject, takeUntil } from "rxjs";
import { ViewRegionComponent } from 'src/app/modules/base/pattern/view-region';
import { Pattern } from "../../pattern/pattern";
import { AccordionComponent } from "./accordion.component";
import { ModelRegionDirective } from "../../pattern/model-region";

export class AccordionPattern extends Pattern {
    items: any[] = [];

    componentRef!: ComponentRef<AccordionComponent>;
    closed$ = new Subject<any>();
    
    constructor(items: any[]) {
        super();

        this.items = items;
        this.menus = JSON.parse(JSON.stringify(this.items)); 
    }

    override render(region: ViewRegionComponent): void {
        region.container.clear();
        this.componentRef = region.container.createComponent(AccordionComponent);
        this.componentRef.instance.items = this.items;

        this.componentRef.changeDetectorRef.detectChanges();
        
        // handle events
    }

    override renderModel(region: ModelRegionDirective, data?: {} | undefined): void {
        const id = this.nextId(this.menus);

        region.element.nativeElement.id = id;
    }

    prefix = '';
    menus: any[] = [];
    public nextId(menus: any[], prefix: string = ''): any {
        if (menus.length == 0) {
            return '';
        }

        let nextMenu = menus[0];
        let id = prefix + '-' + nextMenu.endpoint;

        if (nextMenu.subItem && nextMenu.subItem.length > 0) {
            return this.nextId(nextMenu.subItem, id);
        }
        
        // remove 1st menu item
        menus.shift();    

        return id;
    }

    public resetId() {
        this.menus = [];
        this.prefix = '';
    }

    override onDataChange(data: any = {}): void {
        this.items = data.items;

        this.componentRef.instance.items = this.items;
    }
}