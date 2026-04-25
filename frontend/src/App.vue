<template>
  <RouterView v-if="route.path === '/login'" />
  <div v-else class="app-shell">
    <aside class="sidebar">
      <h1 class="brand">Al7afez Admin</h1>
      <p class="notice">
        {{ $t("app.tagline") }}
      </p>
      <nav class="nav">
        <RouterLink to="/reports">{{ $t("nav.reports") }}</RouterLink>
        <RouterLink to="/recitations">{{ $t("nav.recitations") }}</RouterLink>
        <RouterLink to="/students">{{ $t("nav.students") }}</RouterLink>
        <RouterLink to="/sheikhs">{{ $t("nav.sheikhs") }}</RouterLink>
        <RouterLink to="/levels">{{ $t("nav.levels") }}</RouterLink>
        <RouterLink to="/groups">{{ $t("nav.groups") }}</RouterLink>
        <RouterLink to="/mistake-types">{{ $t("nav.mistakeTypes") }}</RouterLink>
        <RouterLink to="/users">{{ $t("nav.users") }}</RouterLink>
      </nav>
      <button class="secondary" style="margin-top: auto" type="button" @click="logout">
        {{ $t("login.logout") }}
      </button>
    </aside>
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { useRoute, useRouter, RouterLink, RouterView } from "vue-router";

const route = useRoute();
const router = useRouter();

function logout() {
  localStorage.removeItem("token");
  router.push("/login");
}
</script>
