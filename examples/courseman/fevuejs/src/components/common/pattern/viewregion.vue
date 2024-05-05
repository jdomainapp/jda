<template>
    <div>
        <!-- Replace the placeholder with the actual content(s) provided by the patternService -->
    </div>
</template>

<script>
import PatternService from "./pattern.service";

export default {
    name: "ViewRegion",
    props: {
        name: {
            type: String,
            required: true,
        },

        patternService: {
            type: PatternService,
            required: true,
        },
    },

    mounted() {
        // Replace the placeholder with the actual content
        this.patternService.render(this);
    },
}
</script>