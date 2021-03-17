import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import Keycloak, { KeycloakConfig, KeycloakInstance } from "keycloak-js";
import vuetify from "./plugins/vuetify";

Vue.config.productionTip = false;

const initOptions: KeycloakConfig = {
  url: "https://localhost:8443/auth",
  realm: "beagle",
  clientId: "beagle-frontend"
};

const keycloak: KeycloakInstance = Keycloak(initOptions);

keycloak
  .init({ onLoad: "login-required" })
  .then(auth => {
    if (!auth) {
      window.location.reload();
    } else {
      console.log("Authenticated");

      new Vue({
        router,
        store,
        vuetify,
        render: h => h(App, { props: { keycloak: keycloak } })
      }).$mount("#app");
    }

    //Token Refresh
    setInterval(() => {
      keycloak
        .updateToken(70)
        .then(refreshed => {
          if (refreshed) {
            console.log("Token refreshed" + refreshed);
          } else {
            console.log("Token not refreshed");
          }
        })
        .catch(() => {
          console.log("Failed to refresh token");
        });
    }, 6000);
  })
  .catch(() => {
    console.log("Authenticated Failed");
  });
