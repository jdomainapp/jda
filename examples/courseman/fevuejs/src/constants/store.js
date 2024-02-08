// This file is to store variables that are used in the store
import Vue from "vue";

export const store = Vue.observable({
    formTree: [],
});

export const mutations = {
    // Description: formTree will scan for parent item of this item
    // and add this item behind the parent item in array
    addItem(item) {
        if (item.display !== true) {
            return;
        }

        delete item.display;
        store.formTree.push(item);
    },

    deleteItem(item) {
        const index = store.formTree.findIndex((i) => i.id === item.id);
        if (index > -1) {
            store.formTree.splice(index, 1);
        }
    },
};
