// This file is to store variables that are used in the store
import Vue from "vue";

// display can be number or null
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

        let depth = 0;
        let parentID = item.parentID;

        while (parentID !== "") {
            depth++;
            parentID = store.formTree.find((i) => i.id === parentID).parentID;
        }

        item.depth = depth;

        const parentIndex = store.formTree.findIndex(
            (i) => i.id === item.parentID
        );

        let index = parentIndex + 1;

        while (
            index < store.formTree.length &&
            store.formTree[index].depth >= depth
        ) {
            index++;
        }

        store.formTree.splice(index, 0, item);
    },

    deleteItem(item) {
        const index = store.formTree.findIndex((i) => i.id === item.id);
        if (index !== -1) {
            store.formTree.splice(index, 1);
        }
    },
};
