import PatternConsumer from "../../pattern/pattern.consumer";

export default class AutoSearchConsumer extends PatternConsumer {
    onAction(action, event) {
        switch (action) {
            case "select":
                this.host.searchKeyword = event;
                break;
            case "idChange":
                this.host.searchID = event;
                break;
            default:
                // Debug
                console.log(`action: "${action}". event: "${event}"`);
        }
    }
}
