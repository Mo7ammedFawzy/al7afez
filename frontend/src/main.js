import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import App from "./App.vue";
import i18n from "./i18n";
import "./style.css";

import StudentsView from "./views/StudentsView.vue";
import SheikhsView from "./views/SheikhsView.vue";
import LevelsView from "./views/LevelsView.vue";
import GroupsView from "./views/GroupsView.vue";
import MistakeTypesView from "./views/MistakeTypesView.vue";
import RecitationDocumentsView from "./views/RecitationDocumentsView.vue";

const routes = [
  { path: "/", redirect: "/students" },
  { path: "/students", component: StudentsView },
  { path: "/sheikhs", component: SheikhsView },
  { path: "/levels", component: LevelsView },
  { path: "/groups", component: GroupsView },
  { path: "/mistake-types", component: MistakeTypesView },
  { path: "/recitations", component: RecitationDocumentsView }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

const app = createApp(App);
app.use(router);
app.use(i18n);

const currentLocale = i18n.global.locale.value || i18n.global.locale;
document.documentElement.lang = currentLocale;
document.documentElement.dir = currentLocale === "ar" ? "rtl" : "ltr";

app.mount("#app");
