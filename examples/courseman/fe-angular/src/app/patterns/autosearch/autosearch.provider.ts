import { PatternProvider } from "src/app/pattern/pattern.provider"

export class AutoSearchProvider extends PatternProvider {
    override onRenderRegion(region: string, props?: {}): void {
        switch (region) {
            case "searchbox":
                return this.pattern.render();
        }
    }      
}