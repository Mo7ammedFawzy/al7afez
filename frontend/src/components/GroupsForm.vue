<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ form.id ? $t("groups.edit") : $t("groups.new") }}</h2>
      </div>
      <button class="primary icon" type="button" @click="handleList" :title="$t('common.list')" :aria-label="$t('common.list')">☰</button>
    </div>
    <form class="grid grid-2" @submit.prevent="handleSubmit">
      <div>
        <label>{{ $t("groups.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("groups.code") }}</label>
        <input v-model="form.code" required />
      </div>
      <div>
        <label>{{ $t("groups.level") }}</label>
        <select v-model="form.levelId">
          <option value="">{{ $t("groups.selectLevel") }}</option>
          <option v-for="level in levels" :key="level.id" :value="level.id">
            {{ level.name }}
          </option>
        </select>
      </div>
      <div>
        <label>{{ $t("groups.sheikh") }}</label>
        <select v-model="form.sheikhId">
          <option value="">{{ $t("groups.selectSheikh") }}</option>
          <option v-for="sheikh in sheikhs" :key="sheikh.id" :value="sheikh.id">
            {{ sheikh.name }}
          </option>
        </select>
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="handleCancel">{{ $t("common.cancel") }}</button>
      </div>
    </form>
  </section>
</template>

<script setup>
defineProps({
  form: {
    type: Object,
    required: true
  },
  levels: {
    type: Array,
    default: () => []
  },
  sheikhs: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(["submit", "cancel", "list"]);

function handleSubmit() {
  emit("submit");
}

function handleCancel() {
  emit("cancel");
}

function handleList() {
  emit("list");
}
</script>
