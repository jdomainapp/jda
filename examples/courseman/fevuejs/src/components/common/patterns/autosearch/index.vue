<template>
    <div class="px-0 d-flex justify-content-end col">
        <form class="d-flex justify-content-between form-inline">
            <input placeholder="ID..." type="text" class="mr-1 col-md-4 form-control" v-model="search.id" />
            <input placeholder="Search" type="text" class="mr-1 col-md-6 form-control" value="" v-model="search.keyword" />
            <button type="button" class="btn btn-outline-success" title="Search">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search"
                    viewBox="0 0 16 16">
                    <path
                        d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z" />
                </svg>
            </button>
        </form>
    </div>
</template>

<script>
export default {
    name: "AutoSearch",
    data() {
        return {
            search: {
                id: "",
                keyword: "",
            },
        };
    },

    watch: {
        search: {
            handler() {
                this.$emit("search", this.search);
            },
            deep: true,
        },
    },
}
</script>