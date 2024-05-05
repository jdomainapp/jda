/* eslint-disable no-unused-vars */

// abstract class
export default class PatternProvider {
    consumer;
    pattern;

    onRenderRegion(region, data) {}

    onModelRegion(region, data) {}

    onDataChange(data = {}) {
        this.pattern.onDataChange(data);
    }

    onAction(action, event) {
        this.consumer.onAction(action, event);
    }

    action(action, data = {}) {}

    initModel() {}

    initView() {}
}
