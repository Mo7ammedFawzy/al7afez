<template>
  <PageLayout :title="$t('users.list')" icon="pi-user" :count="items.length">
    <template #actions>
      <Button :label="$t('users.new')" icon="pi pi-plus" @click="emit('new')" />
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
  { key: 'name',     label: t('users.name') },
  { key: 'username', label: t('users.username') },
  { key: 'sheikh',   label: t('users.sheikh') },
]);
</script>
