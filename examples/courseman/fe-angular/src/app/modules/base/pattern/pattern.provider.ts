import { ModelRegionDirective } from "./model-region";
import { Pattern } from "./pattern";
import { PatternConsumer } from "./pattern.consumer";
import { ViewRegionComponent } from "./view-region";

export abstract class PatternProvider {
    consumer!: PatternConsumer;
    pattern!: Pattern;
    
    abstract onRenderRegion(region: ViewRegionComponent, data?: {}): void;

    abstract onModelRegion(region: ModelRegionDirective, data?: {}): void;

    onDataChange(data: any = {}) {
        this.pattern.onDataChange(data);
    }

    onAction(action: string, event: any) {
        this.consumer.onAction(action, event);
    }


    action(action: string, data = {}) {

    }

    initModel() {

    }

    initView() {
        
    }
}
