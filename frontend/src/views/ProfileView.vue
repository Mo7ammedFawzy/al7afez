<template>
  <div class="profile-wrap">
    <section class="card profile-card">
      <div class="avatar">{{ initial }}</div>
      <h2 class="profile-name">{{ name }}</h2>
      <p class="profile-username">{{ username }}</p>
      <Button
        class="logout-btn"
        severity="danger"
        :label="$t('login.logout')"
        icon="pi pi-sign-out"
        @click="logout"
      />
    </section>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import Button from "primevue/button";

const router = useRouter();

const user = JSON.parse(localStorage.getItem("user") || "{}");
const name     = computed(() => user.name     || "—");
const username = computed(() => user.username || "");
const initial  = computed(() => (user.name || "؟")[0]);

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  router.push("/login");
}
</script>

<style scoped>
.profile-wrap {
  display: flex;
  justify-content: center;
  padding: var(--space-10) var(--space-4);
}

.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  width: 100%;
  max-width: 360px;
  padding: var(--space-10) var(--space-8);
}

.avatar {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: var(--color-primary-100);
  color: var(--color-primary-700);
  font-size: 2.4rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-name {
  margin: 0;
  font-size: var(--text-xl);
  font-weight: 700;
}

.profile-username {
  margin: 0;
  font-size: var(--text-sm);
  color: var(--color-ink-soft);
  direction: ltr;
}

.logout-btn {
  margin-top: var(--space-6);
  width: 100%;
  justify-content: center;
}
</style>
