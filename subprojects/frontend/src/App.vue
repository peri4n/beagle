<template>
  <div>
    <h1>Hello World</h1>
    <Debug :keycloak="keycloak" />
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator"
import Debug from "@/components/Debug.vue"
import { KeycloakInstance } from "keycloak-js"

@Component({
  components: { Debug }
})
export default class App extends Vue {
  @Prop() private keycloak!: KeycloakInstance;
}
</script>
<style>
</style>
