<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ form.id ? $t("mistakeTypes.edit") : $t("mistakeTypes.new") }}</h2>
      </div>
      <button class="primary icon" type="button" @click="handleList" :title="$t('common.list')" :aria-label="$t('common.list')">☰</button>
    </div>
    <form class="grid grid-2" @submit.prevent="handleSubmit">
      <div>
        <label>{{ $t("mistakeTypes.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("mistakeTypes.code") }}</label>
        <input v-model="form.code" required />
      </div>
      <div class="field-span-2">
        <label>{{ $t("mistakeTypes.parentType") }}</label>
        <select v-model="form.parentId">
          <option value="">{{ $t("mistakeTypes.noParent") }}</option>
          <option v-for="option in parentOptions" :key="option.id" :value="option.id">
            {{ option.name }}
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
  form:          { type: Object, required: true },
  parentOptions: { type: Array,  default: () => [] },
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
