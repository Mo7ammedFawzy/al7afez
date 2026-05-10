<template>
  <div class="profile-wrap">
    <section class="card profile-card">
      <div class="avatar">{{ initial }}</div>
      <h2 class="profile-name">{{ name }}</h2>
      <p class="profile-username">{{ username }}</p>
      <button class="danger logout-btn" type="button" @click="logout">
        {{ $t("login.logout") }}
      </button>
    </section>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const user = JSON.parse(localStorage.getItem("user") || "{}");
const name = computed(() => user.name || "—");
const username = computed(() => user.username || "");
const initial = computed(() => (user.name || "؟")[0]);

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
  padding: 40px 16px;
}

.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 100%;
  max-width: 360px;
  padding: 40px 32px;
}

.avatar {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: var(--accent-soft);
  color: var(--accent);
  font-size: 2.4rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-name {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 700;
}

.profile-username {
  margin: 0;
  font-size: 0.95rem;
  color: var(--ink-soft);
  direction: ltr;
}

.logout-btn {
  margin-top: 28px;
  width: 100%;
}
</style>
