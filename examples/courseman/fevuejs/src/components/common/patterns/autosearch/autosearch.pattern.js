import Pattern from "../../pattern/pattern";
import AutoSearch from "./index.vue";
import Vue from "vue";

export default class AutoSearchPattern extends Pattern {
    items = [];

    constructor(items) {
        super();
        this.items = items;
    }

    render(region) {
        // Clear the region ( Not used due to render multiple times in the same region(?) )
        // region.$el.innerHTML = "";

        // Configure the AutoSearch
        const AutoSearchComponent = new Vue({
            render: (h) =>
                h(AutoSearch, {
                    props: {
                        items: this.items,
                    },
                    on: {
                        keywordChange: (event) =>
                            // event is the keyword from the search box
                            this.onAction("select", event),

                        // no effect yet
                        // (04/06/2024) now bonus with ID search instead of just keyword
                        idChange: (event) => this.onAction("idChange", event),
                    },
                }),
        });

        // Append the AutoSearchComponent to the region
        region.$el.appendChild(AutoSearchComponent.$mount().$el);
    }

    onDataChange(data) {
        this.items = data.items;
        this.componentRef.instance.items = this.items;
    }
}
