<template>
  <PageLayout :title="$t('sheikhs.list')" icon="pi-graduation-cap" :count="items.length">
    <template #actions>
      <Button :label="$t('sheikhs.new')" icon="pi pi-plus" @click="emit('new')" />
    </template>

    <AppTable
      :columns="columns"
      :items="items"
      :loading="loading"
      :page="page"
      :totalPages="totalPages"
      :totalItems="totalItems"
      @edit="emit('edit', $event)"
      @remove="emit('remove', $event)"
      @changePage="emit('changePage', $event)"
    >
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
  totalItems: { type: Number,  default: 0 },
});

const emit = defineEmits(['edit', 'remove', 'changePage', 'new']);

const columns = computed(() => [
  { key: 'name',        label: t('sheikhs.name') },
  { key: 'gender',      label: t('sheikhs.gender') },
  { key: 'phoneNumber', label: t('sheikhs.phone'),     class: 'ltr' },
  { key: 'birthDate',   label: t('sheikhs.birthDate'), class: 'ltr' },
]);
</script>
