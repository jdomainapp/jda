import { Injectable } from "@angular/core";
import { PatternConsumer } from "./pattern.consumer";
import { ViewRegionComponent } from "./view-region";
import { ModelRegionDirective } from "./model-region";

@Injectable({
    providedIn: 'root'
})
export class PatternService {
    consumers: PatternConsumer[] = [];

    addConsumer(consumer: any) { 
        this.consumers.push(consumer);
    }

    render(region: ViewRegionComponent) {
        this.consumers.forEach(consumer => {
            consumer.onRenderRegion(region);
        });
    }

    renderModelRegion(region: ModelRegionDirective) {
        this.consumers.forEach(consumer => {
            consumer.onModelRegion(region);
        });
    }

    onDataChange(data: any = {}) {
        this.consumers.forEach(consumer => {
            consumer.onDataChange(data);
        });
    }
}
