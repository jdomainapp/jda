import PatternProvider from "../PatternProvider"


export default class SearchProvider extends PatternProvider {
    constructor(props) {
        super(props)
    }

    onRenderRegion(region, props = {}) {
        switch(region) {
            case "searchbox":
                return this.onRenderSearch()
        }
    }

    onRenderSearch() {
        if(this.pattern) {
            return this.pattern.render()
        }
    }

    action(action, props = {}) {
        switch(action) {
            case "search":
                this.actionSearch(props.result)
                break
            case "select":
                this.actionSelect(props.item)
                break
        }
    }

    actionSearch(result) {
        this.consumer.onAction("search", {result: result})
    }

    actionSelect(item) {
        this.consumer.onAction("select", {item: item})
    }

    onAction(action, props={}) {
        switch(action) {
            case "updateContent": 
                this.onActionUpdateContent(props.content)
                break
        }
    }

    onActionUpdateContent(content) {
        this.pattern.content = content
    }
}