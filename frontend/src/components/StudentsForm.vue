<template>
  <PageLayout
    :title="form.id ? $t('students.edit') : $t('students.new')"
    icon="pi-users"
  >
    <template #actions>
      <Button
        :label="$t('common.list')"
        icon="pi pi-list"
        severity="secondary"
        @click="emit('list')"
      />
    </template>

    <form class="grid grid-2" @submit.prevent="emit('submit')">
      <div>
        <label>{{ $t("students.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("students.code") }}</label>
        <input v-model="form.code" required />
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
        <Button
          type="submit"
          :label="form.id ? $t('common.save') : $t('common.create')"
          icon="pi pi-check"
        />
        <Button
          type="button"
          :label="$t('common.cancel')"
          icon="pi pi-times"
          severity="secondary"
          @click="emit('cancel')"
        />
      </div>
    </form>
  </PageLayout>
</template>

<script setup>
import Button from "primevue/button";
import PageLayout from "./PageLayout.vue";

defineProps({
  form:   { type: Object, required: true },
  groups: { type: Array,  default: () => [] },
});

const emit = defineEmits(["submit", "cancel", "list"]);
</script>
