<template>
    <div v-bind:region="modelRegion"></div>
</template>

<script>
import PatternService from './pattern.service';

export default {
    name: 'ModelRegion',
    props: {
        modelRegion: {
            type: String,
            default: ''
        }
    },
    inject: ['element'],
    created() {
        PatternService.renderModelRegion(this);
    }
}
</script>