<template>
    <div class="px-0 d-flex justify-content-end col">
        <form class="position-relative d-flex justify-content-between form-inline">
            <input placeholder="ID..." type="text" class="mr-1 col-md-4 form-control" v-model="searchID" />
            <input placeholder="Search" type="text" class="mr-1 col-md-6 form-control" value="" v-model="searchKeyword" />
            <div class="table-responsive border mt-2" style="position: absolute; top: 100%; left: 0; right: 0; z-index: 1000;"
                v-if="shouldShowPopUp">
                <table class="table table-striped table-hover bg-white">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Result</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in filteredItems" :key="item.id" @click="selectItem(item)" style="cursor: pointer;">
                            <td>{{ item.id }}</td>
                            <td>{{ JSON.stringify(item) }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </form>
    </div>
</template>

<script>
export default {
    name: "AutoSearch",
    props: {
        items: {
            type: Array,
            required: true,
        }
    },

    data() {
        return {
            searchID: "",
            searchKeyword: "",
            openPopUp: 0, // 0 = close, 1 = open, 2 = close after select
        };
    },

    methods: {
        // User can type "    ", but it's meaningless so we trim it
        // Also, "hElLo" is the same as "hello" to make the search case-insensitive
        lowercaseTrim(value) {
            return value.toString().toLowerCase().trim();
        },

        // Check if the new value is the same as the old value
        isTheSame(newVal, oldVal) {
            return this.lowercaseTrim(newVal) === this.lowercaseTrim(oldVal);
        },

        // Select the item
        selectItem(item) {
            this.searchID = item.id;
            this.openPopUp = 2;
        },

        // When user type something, we need to open the pop up
        reOpenPopUp() {
            switch (this.openPopUp) {
                case 0:
                    this.openPopUp = 1;
                    break;
                case 2:
                    this.openPopUp = 0;
                    break;
            }
        },

        // Search for every possible key in the object, including nested object
        recursiveSearch(item) {
            for (let key in item) {
                if (item[key] && typeof item[key] === 'object') {
                    if (this.recursiveSearch(item[key])) {
                        return true;
                    }
                } else if (this.lowercaseTrim(item[key]).includes(this.lowercaseTrim(this.searchKeyword))) {
                    return true;
                }
            }
            return false;
        }
    },

    computed: {
        // Check if we should show the pop up
        shouldShowPopUp() {
            return (this.lowercaseTrim(this.searchKeyword) !== "" || this.lowercaseTrim(this.searchID) !== "") && this.items.length > 0 && this.openPopUp === 1;
        },

        // Filter the items based on the search keyword and search ID
        // ID: item.id
        // Keyword: every possible key in the object, including nested object
        filteredItems() {
            const searchID = this.lowercaseTrim(this.searchID);
            return this.items.filter((item) => {
                const stringID = this.lowercaseTrim(item.id);
                return this.recursiveSearch(item) && stringID.includes(searchID);
            });
        }
    },

    watch: {
        searchID: {
            handler(newVal, oldVal) {
                this.reOpenPopUp();
                if (!this.isTheSame(newVal, oldVal)) {
                    this.$emit("idChange", this.lowercaseTrim(newVal));
                }
            },
        },

        searchKeyword: {
            handler(newVal, oldVal) {
                this.reOpenPopUp();
                if (!this.isTheSame(newVal, oldVal)) {
                    this.$emit("keywordChange", this.lowercaseTrim(newVal));
                }
            },
        },
    },
}
</script>