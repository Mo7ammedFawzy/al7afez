<template>
  <PageLayout :title="$t('levels.list')" icon="pi-list" :count="items.length">
    <template #actions>
      <Button :label="$t('levels.new')" icon="pi pi-plus" @click="emit('new')" />
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
      <template #cell-range="{ item }">
        {{ item.fromSurah }}-{{ item.fromAya }} : {{ item.toSurah }}-{{ item.toAya }}
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
  { key: 'name',                   label: t('levels.name') },
  { key: 'range',                  label: t('levels.range') },
  { key: 'numberOfAyatPerSession', label: t('levels.ayatPerSessionShort') },
]);
</script>
