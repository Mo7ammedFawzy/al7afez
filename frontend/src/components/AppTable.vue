<template>
  <div>
    <table class="table">
      <thead>
        <tr>
          <th v-for="col in columns" :key="col.key" :class="col.class">{{ col.label }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in items" :key="item[rowKey]">
          <td v-for="col in columns" :key="col.key" :class="col.class">
            <slot :name="`cell-${col.key}`" :item="item">{{ item[col.key] ?? '—' }}</slot>
          </td>
          <td>
            <div class="row-actions">
              <Button
                icon="pi pi-pencil"
                severity="secondary"
                text
                rounded
                :title="$t('common.edit')"
                @click="emit('edit', item)"
              />
              <Button
                icon="pi pi-trash"
                severity="danger"
                text
                rounded
                :title="$t('common.delete')"
                @click="emit('remove', item)"
              />
            </div>
          </td>
        </tr>
        <tr v-if="!items.length">
          <td :colspan="columns.length + 1" class="empty-row">{{ $t('common.noData') }}</td>
        </tr>
      </tbody>
    </table>

    <div class="pager">
      <Button
        icon="pi pi-angle-right"
        severity="secondary"
        text
        rounded
        :disabled="page === 0"
        :title="$t('common.prev')"
        @click="emit('changePage', -1)"
      />
      <span>{{ page + 1 }} / {{ totalPages || 1 }}</span>
      <Button
        icon="pi pi-angle-left"
        severity="secondary"
        text
        rounded
        :disabled="page + 1 >= totalPages"
        :title="$t('common.next')"
        @click="emit('changePage', 1)"
      />
    </div>
  </div>
</template>

<script setup>
import Button from 'primevue/button';

defineProps({
  columns:    { type: Array,  required: true },
  items:      { type: Array,  default: () => [] },
  page:       { type: Number, default: 0 },
  totalPages: { type: Number, default: 1 },
  rowKey:     { type: String, default: 'id' },
});

const emit = defineEmits(['edit', 'remove', 'changePage']);
</script>

<style scoped>
.row-actions {
  display: flex;
  gap: var(--space-1);
  justify-content: flex-end;
}

.empty-row {
  text-align: center;
  color: var(--color-ink-muted);
  padding: var(--space-8);
  font-size: var(--text-sm);
}
</style>
