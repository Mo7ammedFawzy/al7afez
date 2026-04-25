<template>
  <div class="login-shell">
    <section class="card login-card">
      <h2>Al7afez Admin</h2>
      <p v-if="error" class="login-error">{{ error }}</p>
      <form @submit.prevent="submit">
        <div>
          <label>{{ $t("login.username") }}</label>
          <input v-model="username" required autocomplete="username" />
        </div>
        <div>
          <label>{{ $t("login.password") }}</label>
          <input v-model="password" type="password" required autocomplete="current-password" />
        </div>
        <div class="button-row">
          <button class="primary" type="submit" :disabled="loading">
            {{ loading ? $t("login.loggingIn") : $t("login.submit") }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { apiPost } from "../services/api";

const router = useRouter();
const username = ref("");
const password = ref("");
const error = ref("");
const loading = ref(false);

async function submit() {
  try {
    loading.value = true;
    error.value = "";
    const response = await apiPost("/auth/login", {
      username: username.value,
      password: password.value
    });
    localStorage.setItem("token", response.token);
    router.push("/");
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-shell {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 100%;
  max-width: 380px;
}
.login-error {
  color: var(--color-danger, #e53e3e);
  margin-bottom: 0.75rem;
}
</style>
