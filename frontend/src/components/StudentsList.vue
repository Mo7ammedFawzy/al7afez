<template>
  <PageLayout :title="$t('students.list')" icon="pi-users" :count="items.length">
    <template #actions>
      <Button :label="$t('students.new')" icon="pi pi-plus" @click="emit('new')" />
    </template>

    <AppTable
      :columns="columns"
      :items="items"
      :loading="loading"
      :page="page"
      :totalPages="totalPages"
      @edit="emit('edit', $event)"
      @remove="emit('remove', $event)"
      @changePage="emit('changePage', $event)"
    >
      <template #cell-recitationGroup="{ item }">
        {{ item.recitationGroup?.name || $t('students.noGroup') }}
      </template>
      <template #cell-level="{ item }">
        {{ item.level?.name || '—' }}
      </template>
      <template #cell-gender="{ item }">
        {{ item.gender === 'MALE' ? $t('common.male') : $t('common.female') }}
      </template>
    </AppTable>
  </PageLayout>
</template>

<script setup>
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import Button from 'primevue/button';
import PageLayout from './PageLayout.vue';
import AppTable from './AppTable.vue';

const { t } = useI18n();

defineProps({
  items:      { type: Array,   default: () => [] },
  loading:    { type: Boolean, default: false },
  page:       { type: Number,  default: 0 },
  totalPages: { type: Number,  default: 1 },
});

const emit = defineEmits(['edit', 'remove', 'changePage', 'new']);

const columns = computed(() => [
  { key: 'name',              label: t('students.name') },
  { key: 'recitationGroup',   label: t('students.group') },
  { key: 'level',             label: t('students.level') },
  { key: 'gender',            label: t('students.gender') },
  { key: 'phoneNumber',       label: t('students.phone'),       class: 'ltr' },
  { key: 'parentPhoneNumber', label: t('students.parentPhone'), class: 'ltr' },
  { key: 'birthDate',         label: t('students.birthDate'),   class: 'ltr' },
]);
</script>
