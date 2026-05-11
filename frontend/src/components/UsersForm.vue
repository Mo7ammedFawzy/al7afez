<template>
  <PageLayout :title="form.id ? $t('users.edit') : $t('users.new')" icon="pi-user">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="handleList" />
    </template>

    <form class="grid grid-2" @submit.prevent="handleSubmit" novalidate>
      <AppInput v-model="form.name"     :label="$t('users.name')"     required :error="errors.name" />
      <AppInput v-model="form.code"     :label="$t('users.code')"              />
      <AppInput v-model="form.username" :label="$t('users.username')" required :error="errors.username" />
      <AppSelect v-model="form.sheikhId" :label="$t('users.sheikh')">
        <option value="">{{ $t("users.selectSheikh") }}</option>
        <option v-for="sheikh in sheikhs" :key="sheikh.id" :value="sheikh.id">{{ sheikh.name }}</option>
      </AppSelect>
      <AppInput
        v-if="!form.id"
        v-model="form.password"
        :label="$t('users.password')"
        type="password"
        required
        :error="errors.password"
      />
      <div class="button-row">
        <Button type="submit" :label="form.id ? $t('common.save') : $t('common.create')" icon="pi pi-check" :loading="submitting" :disabled="submitting" />
        <Button type="button" :label="$t('common.cancel')" icon="pi pi-times" severity="secondary" :disabled="submitting" @click="handleCancel" />
      </div>
    </form>

    <template v-if="form.id">
      <hr class="section-divider" />
      <form class="grid grid-2" @submit.prevent="submitChangePassword" novalidate>
        <AppInput v-model="newPassword" :label="$t('users.newPassword')" type="password" required :error="errors.newPassword" />
        <div class="button-row">
          <Button type="submit" :label="$t('users.changePassword')" icon="pi pi-lock" severity="secondary" :loading="submitting" :disabled="submitting" />
        </div>
      </form>
    </template>
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

const { t } = useI18n();
const confirmDialog = useConfirm();

const props = defineProps({
  form:       { type: Object,  required: true },
  sheikhs:    { type: Array,   default: () => [] },
  submitting: { type: Boolean, default: false },
});

const emit = defineEmits(["submit", "cancel", "list", "changePassword"]);

const newPassword = ref("");
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
  errors.name     = props.form.name?.trim()     ? undefined : t("validation.required");
  errors.username = props.form.username?.trim() ? undefined : t("validation.required");
  errors.password = (!props.form.id && !props.form.password?.trim()) ? t("validation.required") : undefined;
  return !Object.values(errors).some(Boolean);
}

function handleSubmit() {
  if (validate()) emit("submit");
}

function submitChangePassword() {
  errors.newPassword = newPassword.value?.trim() ? undefined : t("validation.required");
  if (errors.newPassword) return;
  emit("changePassword", newPassword.value);
  newPassword.value = "";
}
</script>

<style scoped>
.section-divider {
  border: none;
  border-top: 1px solid rgba(27, 60, 116, 0.1);
  margin: 0;
}
</style>
