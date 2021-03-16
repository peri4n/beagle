<template>
  <div>
    {{ json }}
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { KeycloakInstance } from "keycloak-js"

@Component
export default class Debug extends Vue {
  @Prop() private keycloak!: KeycloakInstance;

  get json() {
    console.log(JSON.stringify(this.keycloak, null, 2))
    return typeof this.keycloak + ": " + JSON.stringify(this.keycloak, null, 4)
  }
}
</script>

<style scoped>
h1 {
  margin: 40px 0 0;
}
</style>
