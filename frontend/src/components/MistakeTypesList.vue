<template>
  <PageLayout :title="$t('mistakeTypes.list')" icon="pi-exclamation-triangle" :count="items.length">
    <template #actions>
      <Button :label="$t('mistakeTypes.new')" icon="pi pi-plus" @click="emit('new')" />
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
      <template #cell-code="{ item }">
        {{ item.code || '—' }}
      </template>
      <template #cell-parent="{ item }">
        {{ item.parent?.name || $t('mistakeTypes.noParent') }}
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
  { key: 'name',   label: t('mistakeTypes.name') },
  { key: 'code',   label: t('mistakeTypes.code') },
  { key: 'parent', label: t('mistakeTypes.parentType') },
]);
</script>
