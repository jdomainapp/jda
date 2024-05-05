// This file is to store variables that are used in the store
import Vue from "vue";

const store = Vue.observable({
    formTree: [],

    // search store will store the query given the id of the item
    // use hashmap
    searchStore: [],
});

export const getters = {
    formTree: store.formTree,
    searchStore: store.searchStore,
};

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
