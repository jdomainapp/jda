import PatternProvider from "../PatternProvider"


export default class AccordionProvider extends PatternProvider {
    constructor() {
        super()
    }

    onRenderRegion(region, mainForm, props = {}) {
        switch(region) {
            case "menu":
                return this.onRenderMenu()
            case "menuItem":
                return this.onRenderMenuItem()
            case "skipMenuItem":
                return this.onSkipMenuItem(props.num)
        }
    }

    onRenderMenu() {
        if(this.pattern) {
            return this.pattern.render()
        }
    }

    onRenderMenuItem() {
        // todo: better to use get props by name
        return this.pattern.rawStructure.getCurrentProps()
    }

    onSkipMenuItem(num) {
        return this.pattern.rawStructure.skip(num)
    }
}