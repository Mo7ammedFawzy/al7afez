<template>
  <div class="app-shell">
    <aside class="sidebar">
      <h1 class="brand">Al7afez Admin</h1>
      <p class="notice">
        {{ $t("nav.recitations") }}
      </p>
      <nav class="nav">
        <RouterLink to="/students">{{ $t("nav.students") }}</RouterLink>
        <RouterLink to="/sheikhs">{{ $t("nav.sheikhs") }}</RouterLink>
        <RouterLink to="/levels">{{ $t("nav.levels") }}</RouterLink>
        <RouterLink to="/groups">{{ $t("nav.groups") }}</RouterLink>
        <RouterLink to="/mistake-types">{{ $t("nav.mistakeTypes") }}</RouterLink>
        <RouterLink to="/recitations">{{ $t("nav.recitations") }}</RouterLink>
      </nav>
    </aside>
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from "vue-router";
</script>
