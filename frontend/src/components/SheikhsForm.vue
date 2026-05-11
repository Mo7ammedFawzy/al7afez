<template>
  <PageLayout :title="form.id ? $t('sheikhs.edit') : $t('sheikhs.new')" icon="pi-graduation-cap">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="handleList" />
    </template>

    <form class="grid grid-2" @submit.prevent="handleSubmit" novalidate>
      <AppInput v-model="form.name"        :label="$t('sheikhs.name')"      required :error="errors.name" />
      <AppInput v-model="form.code"        :label="$t('sheikhs.code')"               />
      <AppDatePicker v-model="form.birthDate" :label="$t('sheikhs.birthDate')"       />
      <AppSelect v-model="form.gender"     :label="$t('sheikhs.gender')">
        <option value="MALE">{{ $t("common.male") }}</option>
        <option value="FEMALE">{{ $t("common.female") }}</option>
      </AppSelect>
      <AppInput v-model="form.phoneNumber" :label="$t('sheikhs.phone')"              />
      <div class="button-row">
        <Button type="submit" :label="form.id ? $t('common.save') : $t('common.create')" icon="pi pi-check" :loading="submitting" :disabled="submitting" />
        <Button type="button" :label="$t('common.cancel')" icon="pi pi-times" severity="secondary" :disabled="submitting" @click="handleCancel" />
      </div>
    </form>
  </PageLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
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
  submitting: { type: Boolean, default: false },
});

const emit = defineEmits(["submit", "cancel", "list"]);

const errors = reactive({});
const initialForm = ref('');
onMounted(() => { initialForm.value = JSON.stringify(props.form); });
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
