<template>
  <h1>{{ msg }}}</h1>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";

@Component
export default class HelloWorld extends Vue {
  @Prop() private msg!: string;
}
</script>

<style scoped>
h1 {
  margin: 40px 0 0;
}
</style>
