<template>
  <section class="card">
    <h2>{{ form.id ? $t("students.edit") : $t("students.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="handleSubmit">
      <div>
        <label>{{ $t("students.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("students.code") }}</label>
        <input v-model="form.code" required :placeholder="$t('students.code')" />
      </div>
      <div>
        <label>{{ $t("students.birthDate") }}</label>
        <input v-model="form.birthDate" type="date" />
      </div>
      <div>
        <label>{{ $t("students.gender") }}</label>
        <select v-model="form.gender">
          <option value="MALE">{{ $t("common.male") }}</option>
          <option value="FEMALE">{{ $t("common.female") }}</option>
        </select>
      </div>
      <div>
        <label>{{ $t("students.phone") }}</label>
        <input v-model="form.phoneNumber" />
      </div>
      <div>
        <label>{{ $t("students.parentPhone") }}</label>
        <input v-model="form.parentPhoneNumber" />
      </div>
      <div>
        <label>{{ $t("students.group") }}</label>
        <select v-model="form.recitationGroupId">
          <option value="">{{ $t("students.noGroup") }}</option>
          <option v-for="group in groups" :key="group.id" :value="group.id">
            {{ group.name }}
          </option>
        </select>
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
  },
  groups: {
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
