import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import App from "./App.vue";
import i18n from "./i18n";
import "./style.css";

import PrimeVue from "primevue/config";
import Aura from "@primeuix/themes/aura";
import { definePreset } from "@primeuix/themes";
import ToastService from "primevue/toastservice";
import ConfirmationService from "primevue/confirmationservice";
import "primeicons/primeicons.css";

const Al7afezTheme = definePreset(Aura, {
  semantic: {
    primary: {
      50:  "#eef4ff",
      100: "#d8e4ff",
      200: "#b5cbff",
      300: "#83a8ff",
      400: "#6791ff",
      500: "#2f65d5",
      600: "#2554b8",
      700: "#1d4296",
      800: "#163275",
      900: "#102358",
      950: "#0a1637",
    },
    fontFamily: "'Cairo', 'Segoe UI', sans-serif",
  },
});

import StudentsView from "./views/StudentsView.vue";
import SheikhsView from "./views/SheikhsView.vue";
import LevelsView from "./views/LevelsView.vue";
import GroupsView from "./views/GroupsView.vue";
import MistakeTypesView from "./views/MistakeTypesView.vue";
import RecitationDocumentsView from "./views/RecitationDocumentsView.vue";
import ReportsView from "./views/ReportsView.vue";
import UsersView from "./views/UsersView.vue";
import LoginView from "./views/LoginView.vue";
import ProfileView from "./views/ProfileView.vue";
import StudentProgressView from "./views/StudentProgressView.vue";

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
  { path: "/users", component: UsersView },
  { path: "/profile", component: ProfileView },
  { path: "/students/:id/progress", component: StudentProgressView }
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
    preset: Al7afezTheme,
    options: {
      darkModeSelector: ".dark",
    },
  },
  ripple: true,
});
app.use(ToastService);
app.use(ConfirmationService);

app.mount("#app");
