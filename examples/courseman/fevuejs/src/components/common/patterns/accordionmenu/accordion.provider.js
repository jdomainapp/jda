import PatternProvider from "../../pattern/pattern.provider";

export default class AccordionProvider extends PatternProvider {
    onRenderRegion(region, data) {
        data;

        switch (region.name) {
            case "menu":
                return this.pattern.render(region);
        }
    }

    onModelRegion(region, data) {
        data;

        switch (region.modelRegion) {
            case "form":
            case "forminput":
                return this.pattern.renderModel(region);
        }
    }
}
