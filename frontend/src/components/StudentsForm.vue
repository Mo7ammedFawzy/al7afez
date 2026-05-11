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
      <AppInput v-model="form.name"              :label="$t('students.name')"        required />
      <AppInput v-model="form.code"              :label="$t('students.code')"        required />
      <AppDatePicker v-model="form.birthDate"    :label="$t('students.birthDate')"            />
      <AppSelect v-model="form.gender"           :label="$t('students.gender')">
        <option value="MALE">{{ $t("common.male") }}</option>
        <option value="FEMALE">{{ $t("common.female") }}</option>
      </AppSelect>
      <AppInput v-model="form.phoneNumber"       :label="$t('students.phone')"                />
      <AppInput v-model="form.parentPhoneNumber" :label="$t('students.parentPhone')"          />
      <AppSelect v-model="form.recitationGroupId" :label="$t('students.group')">
        <option value="">{{ $t("students.noGroup") }}</option>
        <option v-for="group in groups" :key="group.id" :value="group.id">
          {{ group.name }}
        </option>
      </AppSelect>
      <div class="button-row">
        <Button
          type="submit"
          :label="form.id ? $t('common.save') : $t('common.create')"
          icon="pi pi-check"
          :loading="submitting"
          :disabled="submitting"
        />
        <Button
          type="button"
          :label="$t('common.cancel')"
          icon="pi pi-times"
          severity="secondary"
          :disabled="submitting"
          @click="emit('cancel')"
        />
      </div>
    </form>
  </PageLayout>
</template>

<script setup>
import Button from "primevue/button";
import PageLayout from "./PageLayout.vue";
import AppInput from "./AppInput.vue";
import AppSelect from "./AppSelect.vue";
import AppDatePicker from "./AppDatePicker.vue";

defineProps({
  form:       { type: Object,  required: true },
  groups:     { type: Array,   default: () => [] },
  submitting: { type: Boolean, default: false },
});

const emit = defineEmits(["submit", "cancel", "list"]);
</script>
