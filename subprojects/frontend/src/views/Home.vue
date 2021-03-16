<template>
  <HelloWorld msg="Welcome to Your Vue.js + TypeScript App" />
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Debug from "@/components/Debug.vue";

@Component({
  components: {
    HelloWorld: Debug
  }
})
export default class Home extends Vue {}
</script>
