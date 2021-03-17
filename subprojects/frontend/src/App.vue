<template>
  <v-app id="inspire">
    <v-navigation-drawer
        v-model="drawer"
        app
    >
      <!--  -->
    </v-navigation-drawer>

    <v-app-bar app>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>

      <v-toolbar-title>Application</v-toolbar-title>
    </v-app-bar>

    <v-main>
      <!--  -->
      <Debug :keycloak="keycloak" />
    </v-main>
  </v-app>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator"
import Debug from "@/components/Debug.vue"
import { KeycloakInstance } from "keycloak-js"

@Component({
  components: { Debug }
})
export default class App extends Vue {

  private drawer = false

  @Prop() private keycloak!: KeycloakInstance;

}
</script>
<style>
</style>
