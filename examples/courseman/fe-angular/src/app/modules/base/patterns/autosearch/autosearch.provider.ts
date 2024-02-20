import { PatternProvider } from "src/app/modules/base/pattern/pattern.provider"
import { ViewRegionComponent } from "../../pattern/view-region";
import { ModelRegionDirective } from "../../pattern/model-region";

export class AutoSearchProvider extends PatternProvider {
    
    override onRenderRegion(region: ViewRegionComponent, data?: {}): void {
        switch (region.name) {
            case "searchbox":
                return this.pattern.render(region);
        }
    }

    override onModelRegion(region: ModelRegionDirective, data?: {}): void {
        // do nothing
    }
    
}