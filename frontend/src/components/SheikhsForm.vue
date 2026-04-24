<template>
  <section class="card">
    <h2>{{ form.id ? $t("sheikhs.edit") : $t("sheikhs.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="handleSubmit">
      <div>
        <label>{{ $t("sheikhs.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("sheikhs.code") }}</label>
        <input v-model="form.code" required />
      </div>
      <div>
        <label>{{ $t("sheikhs.birthDate") }}</label>
        <input v-model="form.birthDate" type="date" />
      </div>
      <div>
        <label>{{ $t("sheikhs.gender") }}</label>
        <select v-model="form.gender">
          <option value="MALE">{{ $t("common.male") }}</option>
          <option value="FEMALE">{{ $t("common.female") }}</option>
        </select>
      </div>
      <div>
        <label>{{ $t("sheikhs.phone") }}</label>
        <input v-model="form.phoneNumber" />
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="handleCancel">{{ $t("common.cancel") }}</button>
        <button class="secondary" type="button" @click="handleList">{{ $t("common.list") }}</button>
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
