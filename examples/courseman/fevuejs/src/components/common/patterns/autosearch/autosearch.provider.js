/* eslint-disable no-unused-vars */
import PatternProvider from "../../pattern/pattern.provider";

export default class AutoSearchProvider extends PatternProvider {

    onRenderRegion(region, data) {
        switch (region.name) {
            case "searchbox":
                return this.pattern.render(region);
        }
    }

    onModelRegion(region, data) {
        // do nothing
    }
}
