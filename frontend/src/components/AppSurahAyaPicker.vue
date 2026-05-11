<template>
  <div class="app-field">
    <label v-if="label" :for="`${uid}-surah`">
      {{ label }}<span v-if="required" class="req" aria-hidden="true">*</span>
    </label>
    <div class="surah-aya-row">
      <Select
        :inputId="`${uid}-surah`"
        :modelValue="surah || null"
        :options="surahOptions"
        optionLabel="label"
        optionValue="number"
        filter
        :filterPlaceholder="$t('common.search')"
        :placeholder="$t('common.selectSurah')"
        class="surah-select"
        @update:modelValue="onSurahChange"
      />
      <input
        class="aya-input"
        type="number"
        min="1"
        :max="maxAya ?? undefined"
        :placeholder="$t('common.aya')"
        :aria-label="$t('common.aya')"
        :disabled="!surah"
        :value="aya || ''"
        @input="onAyaInput"
      />
      <span v-if="maxAya" class="aya-max" aria-hidden="true">/ {{ maxAya }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance } from 'vue';
import Select from 'primevue/select';
import { SURAHS, ayaCountForSurah } from '../data/surahs.js';

const uid = `app-field-${getCurrentInstance().uid}`;

const surahOptions = SURAHS.map(s => ({ ...s, label: `${s.number} - ${s.name}` }));

const props = defineProps({
  label:    { type: String,  default: undefined },
  surah:    { type: Number,  default: null },
  aya:      { type: Number,  default: null },
  required: { type: Boolean, default: false },
});

const emit = defineEmits(['update:surah', 'update:aya']);

const maxAya = computed(() => props.surah ? ayaCountForSurah(props.surah) : null);

function onSurahChange(newSurah) {
  emit('update:surah', newSurah);
  const max = ayaCountForSurah(newSurah);
  if (props.aya && max && props.aya > max) {
    emit('update:aya', max);
  }
}

function onAyaInput(e) {
  const val = parseInt(e.target.value, 10);
  if (!val || val < 1) return;
  const clamped = maxAya.value ? Math.min(val, maxAya.value) : val;
  emit('update:aya', clamped);
}
</script>

<style scoped>
.surah-aya-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.surah-select {
  flex: 1 1 0;
  min-width: 0;
}

.aya-input {
  width: 5rem;
  flex-shrink: 0;
  text-align: center;
}

.aya-max {
  font-size: var(--text-sm);
  color: var(--color-ink-muted);
  white-space: nowrap;
  flex-shrink: 0;
}
</style>
