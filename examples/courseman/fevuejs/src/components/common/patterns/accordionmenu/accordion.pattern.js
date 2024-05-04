import Pattern from "../../pattern/pattern";
import Accordion from "./template/index.vue";
import Vue from "vue";

export default class AccordionPattern extends Pattern {
    items = [];
    forms = {};

    constructor(items) {
        super();
        this.items = items;
    }

    render(region) {
        const AccordionComponent = new Vue({
            render: (h) =>
                h(Accordion, {
                    // add stuff later
                }),
        });

        region.$el.appendChild(AccordionComponent.$mount().$el);
    }

    // todo: rewrite into static method
    renderModel(region, data = undefined) {
        data;

        console.log("model");
        const nativeElement = region.$el;
        let id = nativeElement.getAttribute("id");

        if (!id) return;

        // get outer form id
        const form = nativeElement.parentElement.closest("form");

        // update id if not rendered
        if (form && !nativeElement.hasAttribute("rendered")) {
            id = form.getAttribute("id") + "-" + nativeElement.id;
            nativeElement.id = id;
        }

        // if form element
        if (nativeElement.tagName.toLowerCase() === "form") {
            const item = { endpoint: id, name: id, subItem: [] };
            this.forms[id] = item;

            // if outer form
            if (form) {
                let _item = this.forms[form.getAttribute("id")];
                _item.subItem.push(item);
            } else {
                // outmost level
                this.items.push(item);
            }
        } else {
            // forminput element
            // label
            const label = nativeElement.parentElement.querySelector("label");

            const _item = this.forms[form.getAttribute("id")];
            _item.subItem.push({ endpoint: id, name: label.textContent });
        }

        // marked as rendered
        nativeElement.setAttribute("rendered", true);
    }

    onDataChange(data = {}) {
        data;
        // this.items = data.items;
        // this.componentRef.instance.items = this.items;
    }
}
