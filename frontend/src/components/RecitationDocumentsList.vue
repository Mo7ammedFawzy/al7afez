<template>
  <PageLayout :title="$t('recitations.list')" icon="pi-book" :count="items.length">
    <template #actions>
      <Button :label="$t('recitations.new')" icon="pi pi-plus" @click="emit('new')" />
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
      <template #cell-student="{ item }">
        {{ item.student?.name || '—' }}
      </template>
      <template #cell-group="{ item }">
        {{ item.group?.name || '—' }}
      </template>
      <template #cell-range="{ item }">
        {{ rangeLabel(item) }}
      </template>
      <template #cell-mistakes="{ item }">
        {{ joinNames(item.mistakes) }}
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
  { key: 'recitationDate', label: t('recitations.recitationDate') },
  { key: 'student',        label: t('recitations.student') },
  { key: 'group',          label: t('groups.group') },
  { key: 'range',          label: t('recitations.range') },
  { key: 'numberOfAyat',   label: t('recitations.ayat') },
  { key: 'grade',          label: t('recitations.grade') },
  { key: 'totalMistakes',  label: t('recitations.totalMistakes') },
  { key: 'mistakes',       label: t('common.mistakes') },
]);

function joinNames(selected) {
  if (!selected?.length) return '';
  return selected
    .map(item => `${item.mistakeType?.name || ''} (${item.count})`)
    .filter(Boolean)
    .join('، ');
}

function rangeLabel(recitation) {
  const start = recitation.fromSurah && recitation.fromAya
    ? `${recitation.fromSurah}:${recitation.fromAya}`
    : '—';
  const end = recitation.toSurah && recitation.toAya
    ? `${recitation.toSurah}:${recitation.toAya}`
    : '—';
  return `${start} – ${end}`;
}
</script>
