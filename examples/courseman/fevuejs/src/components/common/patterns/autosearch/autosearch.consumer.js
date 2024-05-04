import PatternConsumer from "../../pattern/pattern.consumer";

export default class AutoSearchConsumer extends PatternConsumer {
    onAction(action, event) {
        // console.log(`action: "${action}". event: "${event}"`);
        this.host.searchKeyword = event;
    }
}
