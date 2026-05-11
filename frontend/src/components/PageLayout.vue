<template>
  <section class="page-layout">
    <div class="page-layout-header">
      <div class="page-layout-title">
        <span v-if="icon" :class="['page-layout-icon', 'pi', icon]" />
        <h2 class="page-layout-heading">{{ title }}</h2>
        <span v-if="count !== undefined" class="badge">{{ count }}</span>
      </div>
      <div class="page-layout-actions">
        <slot name="actions" />
      </div>
    </div>

    <slot />
  </section>
</template>

<script setup>
defineProps({
  title:  { type: String,  required: true },
  icon:   { type: String,  default: undefined },
  count:  { type: Number,  default: undefined },
});
</script>

<style scoped>
.page-layout {
  background: rgba(255, 255, 255, 0.96);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid rgba(27, 60, 116, 0.08);
  box-shadow: var(--shadow-lg);
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.page-layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  flex-wrap: wrap;
}

.page-layout-title {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  min-width: 0;
}

.page-layout-icon {
  font-size: var(--text-xl);
  color: var(--color-primary-500);
  flex-shrink: 0;
}

.page-layout-heading {
  margin: 0;
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--color-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.page-layout-actions {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  flex-shrink: 0;
}

@media (max-width: 600px) {
  .page-layout {
    padding: var(--space-4);
    gap: var(--space-4);
    border-radius: var(--radius-lg);
  }

  .page-layout-heading {
    font-size: var(--text-lg);
  }
}
</style>
