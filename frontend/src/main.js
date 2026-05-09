import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import App from "./App.vue";
import i18n from "./i18n";
import "./style.css";

import PrimeVue from "primevue/config";
import Aura from "@primeuix/themes/aura";
import ToastService from "primevue/toastservice";
import ConfirmationService from "primevue/confirmationservice";
import "primeicons/primeicons.css";

import StudentsView from "./views/StudentsView.vue";
import SheikhsView from "./views/SheikhsView.vue";
import LevelsView from "./views/LevelsView.vue";
import GroupsView from "./views/GroupsView.vue";
import MistakeTypesView from "./views/MistakeTypesView.vue";
import RecitationDocumentsView from "./views/RecitationDocumentsView.vue";
import ReportsView from "./views/ReportsView.vue";
import UsersView from "./views/UsersView.vue";
import LoginView from "./views/LoginView.vue";

const routes = [
  { path: "/login", component: LoginView, meta: { public: true } },
  { path: "/", redirect: "/reports" },
  { path: "/reports", component: ReportsView },
  { path: "/students", component: StudentsView },
  { path: "/sheikhs", component: SheikhsView },
  { path: "/levels", component: LevelsView },
  { path: "/groups", component: GroupsView },
  { path: "/mistake-types", component: MistakeTypesView },
  { path: "/recitations", component: RecitationDocumentsView },
  { path: "/users", component: UsersView }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to) => {
  const loggedIn = !!localStorage.getItem("token");
  if (!to.meta.public && !loggedIn) return "/login";
  if (to.path === "/login" && loggedIn) return "/";
});

const app = createApp(App);
app.use(router);
app.use(i18n);
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: ".dark",
    },
  },
  ripple: true,
});
app.use(ToastService);
app.use(ConfirmationService);

const currentLocale = i18n.global.locale.value || i18n.global.locale;
document.documentElement.lang = currentLocale;
document.documentElement.dir = currentLocale === "ar" ? "rtl" : "ltr";

app.mount("#app");
