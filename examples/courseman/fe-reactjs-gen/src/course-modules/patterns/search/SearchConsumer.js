import Pattern from "../../../common/patterns/Pattern";
import PatternConsumer from "../../../common/patterns/PatternConsumer";

export default class SearchConsumer extends PatternConsumer {
    constructor(props) {
        super(props)
        this.mainForm = props.mainForm
    }

    onAction(action, props={}) {
        switch(action) {
            case "search":
                this.onActionSearch(props.result)
                break
            case "select":
                this.onActionSelect(props.item)
                break
        }
    }

    onActionSearch(result) {
        this.mainForm.handleStateChange("displayingContent", result, false)
    }

    onActionSelect(item) {
        if(item.length === 1) {
            console.log(item)
            this.mainForm.handleStateChange("viewType", "details")
            this.mainForm.handleStateChange(
                "currentId", item[0].id, true);
        }
    }

    action(action, props = {}) {
        switch(action) {
            case "updateContent":
                this.actionUpdateContent(props.content)
        }
    }

    actionUpdateContent(content) {
        console.log(content)
        this.provider.onActionUpdateContent(content)
    }
}