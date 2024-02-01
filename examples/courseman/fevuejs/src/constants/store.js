// This file is to store variables that are used in the store
import Vue from "vue";

// This map is to store the last index of item that has the same parentID
let lastIndexMap = new Map();

// This map is to store the index of item in formTree
let indexMap = new Map();

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

        let index = lastIndexMap.get(item.parentID);
        if (index === undefined) {
            index = store.formTree.length;
        } else {
            index++;
        }

        store.formTree.splice(index, 0, item);
        lastIndexMap.set(item.parentID, index);

        // Update the index map
        for (let i = index; i < store.formTree.length; i++) {
            indexMap.set(store.formTree[i].id, i);
        }
    },

    deleteItem(item) {
        const index = indexMap.get(item.id);
        if (index !== undefined) {
            store.formTree.splice(index, 1);
            indexMap.delete(item.id);

            // Update the index map
            for (let i = index; i < store.formTree.length; i++) {
                indexMap.set(store.formTree[i].id, i);
            }
        }
    },
};
