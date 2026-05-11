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
        <template v-if="loading">
          <tr v-for="n in 5" :key="`skel-${n}`" class="skel-row">
            <td v-for="col in columns" :key="col.key"><Skeleton height="1.1rem" /></td>
            <td><Skeleton height="1.1rem" width="80px" /></td>
          </tr>
        </template>
        <template v-else>
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
                  :aria-label="$t('common.edit')"
                  @click="emit('edit', item)"
                />
                <Button
                  icon="pi pi-trash"
                  severity="danger"
                  text
                  rounded
                  :aria-label="$t('common.delete')"
                  @click="emit('remove', item)"
                />
              </div>
            </td>
          </tr>
          <tr v-if="!items.length">
            <td :colspan="columns.length + 1">
              <div class="empty-state">
                <span class="pi pi-inbox empty-icon" />
                <p class="empty-text">{{ $t('common.noData') }}</p>
              </div>
            </td>
          </tr>
        </template>
      </tbody>
    </table>

    <div class="pager">
      <span v-if="totalItems > 0" class="pager-range">
        {{ $t('common.showingRange', { from: rangeFrom, to: rangeTo, total: totalItems }) }}
      </span>
      <Button
        icon="pi pi-angle-right"
        severity="secondary"
        text
        rounded
        :disabled="page === 0 || loading"
        :aria-label="$t('common.prev')"
        @click="emit('changePage', -1)"
      />
      <span class="pager-pages">{{ page + 1 }} / {{ totalPages || 1 }}</span>
      <Button
        icon="pi pi-angle-left"
        severity="secondary"
        text
        rounded
        :disabled="page + 1 >= totalPages || loading"
        :aria-label="$t('common.next')"
        @click="emit('changePage', 1)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import Button from 'primevue/button';
import Skeleton from 'primevue/skeleton';

const props = defineProps({
  columns:    { type: Array,   required: true },
  items:      { type: Array,   default: () => [] },
  loading:    { type: Boolean, default: false },
  page:       { type: Number,  default: 0 },
  totalPages: { type: Number,  default: 1 },
  totalItems: { type: Number,  default: 0 },
  pageSize:   { type: Number,  default: 10 },
  rowKey:     { type: String,  default: 'id' },
});

const emit = defineEmits(['edit', 'remove', 'changePage']);

const rangeFrom = computed(() => props.totalItems === 0 ? 0 : props.page * props.pageSize + 1);
const rangeTo   = computed(() => Math.min(props.page * props.pageSize + props.items.length, props.totalItems));
</script>

<style scoped>
.row-actions {
  display: flex;
  gap: var(--space-1);
  justify-content: flex-end;
}

.skel-row td {
  padding: var(--space-3) var(--space-4);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-12) var(--space-8);
  color: var(--color-ink-muted);
}

.pager-range {
  font-size: var(--text-sm);
  color: var(--color-ink-muted);
  flex: 1;
}

.pager-pages {
  font-size: var(--text-sm);
  color: var(--color-ink-soft);
}

.empty-icon {
  font-size: 2rem;
  opacity: 0.35;
}

.empty-text {
  margin: 0;
  font-size: var(--text-sm);
}
</style>
