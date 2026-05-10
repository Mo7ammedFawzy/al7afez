<template>
  <PageLayout :title="$t('groups.list')" icon="pi-sitemap" :count="items.length">
    <template #actions>
      <Button :label="$t('groups.new')" icon="pi pi-plus" @click="emit('new')" />
    </template>

    <AppTable
      :columns="columns"
      :items="items"
      :page="page"
      :totalPages="totalPages"
      @edit="emit('edit', $event)"
      @remove="emit('remove', $event)"
      @changePage="emit('changePage', $event)"
    >
      <template #cell-level="{ item }">
        {{ item.level?.name || '—' }}
      </template>
      <template #cell-sheikh="{ item }">
        {{ item.sheikh?.name || '—' }}
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
  items:      { type: Array,  default: () => [] },
  page:       { type: Number, default: 0 },
  totalPages: { type: Number, default: 1 },
});

const emit = defineEmits(['edit', 'remove', 'changePage', 'new']);

const columns = computed(() => [
  { key: 'name',   label: t('groups.name') },
  { key: 'level',  label: t('groups.level') },
  { key: 'sheikh', label: t('groups.sheikh') },
]);
</script>
