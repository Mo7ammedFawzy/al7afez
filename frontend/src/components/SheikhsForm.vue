<template>
  <PageLayout :title="form.id ? $t('sheikhs.edit') : $t('sheikhs.new')" icon="pi-graduation-cap">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="emit('list')" />
    </template>

    <form class="grid grid-2" @submit.prevent="emit('submit')">
      <AppInput v-model="form.name"        :label="$t('sheikhs.name')"      required />
      <AppInput v-model="form.code"        :label="$t('sheikhs.code')"               />
      <AppDatePicker v-model="form.birthDate" :label="$t('sheikhs.birthDate')"       />
      <AppSelect v-model="form.gender"     :label="$t('sheikhs.gender')">
        <option value="MALE">{{ $t("common.male") }}</option>
        <option value="FEMALE">{{ $t("common.female") }}</option>
      </AppSelect>
      <AppInput v-model="form.phoneNumber" :label="$t('sheikhs.phone')"              />
      <div class="button-row">
        <Button type="submit" :label="form.id ? $t('common.save') : $t('common.create')" icon="pi pi-check" />
        <Button type="button" :label="$t('common.cancel')" icon="pi pi-times" severity="secondary" @click="emit('cancel')" />
      </div>
    </form>
  </PageLayout>
</template>

<script setup>
import Button from 'primevue/button';
import PageLayout from './PageLayout.vue';
import AppInput from './AppInput.vue';
import AppSelect from './AppSelect.vue';
import AppDatePicker from './AppDatePicker.vue';

defineProps({
  form: { type: Object, required: true },
});

const emit = defineEmits(['submit', 'cancel', 'list']);
</script>
