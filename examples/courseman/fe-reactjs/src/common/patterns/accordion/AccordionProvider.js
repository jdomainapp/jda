import PatternProvider from "../PatternProvider"


export default class AccordionProvider extends PatternProvider {
    constructor() {
        super()
    }

    onRenderRegion(region, mainForm) {
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

    onModelRegion({mainForm, region, props = []}) {
        switch(region) {
            case "menuItem":
                return this.onModelMenuItem()
        }
    }

    onModelMenuItem() {
        // todo: better to use get props by name
        return this.pattern.state.modules.getCurrentProps()
    }
}