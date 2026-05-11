<template>
  <PageLayout :title="form.id ? $t('mistakeTypes.edit') : $t('mistakeTypes.new')" icon="pi-exclamation-triangle">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="emit('list')" />
    </template>

    <form class="grid grid-2" @submit.prevent="handleSubmit" novalidate>
      <AppInput v-model="form.code" :label="$t('mistakeTypes.code')" required />
      <AppInput v-model="form.name" :label="$t('mistakeTypes.name')" required :error="errors.name" />
      <AppMistakeTypeSelect
        class="field-span-2"
        :label="$t('mistakeTypes.parentType')"
        :options="parentOptions"
        :placeholder="$t('mistakeTypes.noParent')"
        v-model="form.parentId"
      />
      <div class="button-row">
        <Button type="submit" :label="form.id ? $t('common.save') : $t('common.create')" icon="pi pi-check" :loading="submitting" :disabled="submitting" />
        <Button type="button" :label="$t('common.cancel')" icon="pi pi-times" severity="secondary" :disabled="submitting" @click="emit('cancel')" />
      </div>
    </form>
  </PageLayout>
</template>

<script setup>
import { reactive } from "vue";
import { useI18n } from "vue-i18n";
import Button from "primevue/button";
import PageLayout from "./PageLayout.vue";
import AppInput from "./AppInput.vue";
import AppMistakeTypeSelect from "./AppMistakeTypeSelect.vue";

const { t } = useI18n();

const props = defineProps({
  form:          { type: Object,  required: true },
  parentOptions: { type: Array,   default: () => [] },
  submitting:    { type: Boolean, default: false },
});

const emit = defineEmits(["submit", "cancel", "list"]);

const errors = reactive({});

function validate() {
  errors.name = props.form.name?.trim() ? undefined : t("validation.required");
  return !Object.values(errors).some(Boolean);
}

function handleSubmit() {
  if (validate()) emit("submit");
}
</script>
