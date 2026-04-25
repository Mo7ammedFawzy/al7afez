<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ form.id ? $t("users.edit") : $t("users.new") }}</h2>
      </div>
      <button class="primary icon" type="button" @click="handleList" :title="$t('common.list')" :aria-label="$t('common.list')">☰</button>
    </div>
    <form class="grid grid-2" @submit.prevent="handleSubmit">
      <div>
        <label>{{ $t("users.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("users.code") }}</label>
        <input v-model="form.code" />
      </div>
      <div>
        <label>{{ $t("users.username") }}</label>
        <input v-model="form.username" required />
      </div>
      <div>
        <label>{{ $t("users.sheikh") }}</label>
        <select v-model="form.sheikhId">
          <option value="">{{ $t("users.selectSheikh") }}</option>
          <option v-for="sheikh in sheikhs" :key="sheikh.id" :value="sheikh.id">
            {{ sheikh.name }}
          </option>
        </select>
      </div>
      <div v-if="!form.id">
        <label>{{ $t("users.password") }}</label>
        <input v-model="form.password" type="password" required />
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="handleCancel">{{ $t("common.cancel") }}</button>
      </div>
    </form>

    <template v-if="form.id">
      <hr />
      <form class="grid grid-2" @submit.prevent="handleChangePassword">
        <div>
          <label>{{ $t("users.newPassword") }}</label>
          <input v-model="newPassword" type="password" required />
        </div>
        <div class="button-row">
          <button class="primary" type="submit">{{ $t("users.changePassword") }}</button>
        </div>
      </form>
    </template>
  </section>
</template>

<script setup>
import { ref } from "vue";

defineProps({
  form: {
    type: Object,
    required: true
  },
  sheikhs: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(["submit", "cancel", "list", "changePassword"]);

const newPassword = ref("");

function handleSubmit() {
  emit("submit");
}

function handleCancel() {
  emit("cancel");
}

function handleList() {
  emit("list");
}

function handleChangePassword() {
  emit("changePassword", newPassword.value);
  newPassword.value = "";
}
</script>