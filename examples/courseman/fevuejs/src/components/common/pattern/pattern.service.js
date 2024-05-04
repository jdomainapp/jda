export default class PatternService {
    consumers = [];

    addConsumer(consumer) {
        this.consumers.push(consumer);
    }

    render(region) {
        this.consumers.forEach((consumer) => {
            consumer.onRenderRegion(region);
        });
    }

    renderModelRegion(region) {
        this.consumers.forEach((consumer) => {
            consumer.onModelRegion(region);
        });
    }

    onDataChange(data = {}) {
        this.consumers.forEach((consumer) => {
            consumer.onDataChange(data);
        });
    }
}
