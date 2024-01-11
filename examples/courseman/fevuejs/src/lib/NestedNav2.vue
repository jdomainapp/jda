<template>
    <div>
        {{ JSON.stringify(store) }}
    </div>
</template>

<script>
import { store } from "../constants/store";

export default {
    props: ["searchQuery"],
    data() {
        return {
            store: store.tree,
        };
    },
};
</script>
