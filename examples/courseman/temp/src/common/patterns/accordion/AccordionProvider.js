import PatternProvider from "../PatternProvider"


export default class AccordionProvider extends PatternProvider {
    constructor() {
        super()
    }

    onRenderRegion(region, props = {}) {
        switch(region) {
            case "menu":
                return this.onRenderMenu()
        }
    }

    onRenderMenu() {
        if(this.pattern) {
            return this.pattern.render()
        }
    }

    onModelRegion(region, props = {}) {
        switch(region) {
            case "menuItem":
                return this.onModelMenuItem()
            case "skipMenuItem":
                return this.onSkipMenuItem(props.num)
        }
    }

    onModelMenuItem() {
        // todo: better to use get props by name
        return this.pattern.rawStructure.getCurrentProps()
    }

    onSkipMenuItem(num) {
        return this.pattern.rawStructure.skip(num)
    }
}