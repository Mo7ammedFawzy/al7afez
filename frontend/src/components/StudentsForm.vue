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
        @click="handleList"
      />
    </template>

    <form ref="formEl" class="grid grid-2" @submit.prevent="handleSubmit" novalidate>
      <AppInput v-model="form.name"              :label="$t('students.name')"        required :error="errors.name" />
      <AppInput v-model="form.code"              :label="$t('students.code')"                 />
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
          @click="handleCancel"
        />
      </div>
    </form>
  </PageLayout>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useConfirm } from "primevue/useconfirm";
import Button from "primevue/button";
import PageLayout from "./PageLayout.vue";
import AppInput from "./AppInput.vue";
import AppSelect from "./AppSelect.vue";
import AppDatePicker from "./AppDatePicker.vue";

const { t } = useI18n();
const confirmDialog = useConfirm();

const props = defineProps({
  form:       { type: Object,  required: true },
  groups:     { type: Array,   default: () => [] },
  submitting: { type: Boolean, default: false },
});

const emit = defineEmits(["submit", "cancel", "list"]);

const errors = reactive({});
const initialForm = ref('');
const formEl = ref(null);
onMounted(() => {
  initialForm.value = JSON.stringify(props.form);
  nextTick(() => formEl.value?.querySelector('input:not([disabled]), select:not([disabled])')?.focus());
});
const isDirty = computed(() => JSON.stringify(props.form) !== initialForm.value);

function confirmDiscard(action) {
  if (!isDirty.value) { action(); return; }
  confirmDialog.require({
    message: t('common.unsavedChanges'),
    header: t('common.discardTitle'),
    icon: 'pi pi-exclamation-triangle',
    acceptProps: { severity: 'danger' },
    rejectProps: { severity: 'secondary', outlined: true },
    acceptLabel: t('common.discard'),
    rejectLabel: t('common.cancel'),
    accept: action,
  });
}

function handleCancel() { confirmDiscard(() => emit('cancel')); }
function handleList()   { confirmDiscard(() => emit('list'));   }

function validate() {
  errors.name = props.form.name?.trim() ? undefined : t("validation.required");
  return !Object.values(errors).some(Boolean);
}

function handleSubmit() {
  if (validate()) emit("submit");
}
</script>
