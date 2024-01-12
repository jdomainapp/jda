import { PatternConsumer } from "src/app/pattern/pattern.consumer";

export class AutoSearchConsumer extends PatternConsumer {
    override onRenderRegion(region: string, props?: {}): void {
        switch (region) {
            case "searchbox":
                return this.onRenderSearch();
        }
    }      
}