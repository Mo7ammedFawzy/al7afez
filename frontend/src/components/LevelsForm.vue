<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ form.id ? $t("levels.edit") : $t("levels.new") }}</h2>
      </div>
      <button class="primary icon" type="button" @click="handleCancel" :title="$t('common.list')" :aria-label="$t('common.list')">☰</button>
    </div>
    <form class="grid grid-2" @submit.prevent="handleSubmit">
      <div>
        <label>{{ $t("levels.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("levels.code") }}</label>
        <input v-model="form.code" required />
      </div>
      <div>
        <label>{{ $t("levels.fromSurah") }}</label>
        <input v-model.number="form.fromSurah" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("levels.toSurah") }}</label>
        <input v-model.number="form.toSurah" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("levels.fromAya") }}</label>
        <input v-model.number="form.fromAya" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("levels.toAya") }}</label>
        <input v-model.number="form.toAya" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("levels.ayatPerSession") }}</label>
        <input v-model.number="form.numberOfAyatPerSession" type="number" min="1" />
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
  }
});

const emit = defineEmits(["submit", "cancel"]);

function handleSubmit() {
  emit("submit");
}

function handleCancel() {
  emit("cancel");
}
</script>
