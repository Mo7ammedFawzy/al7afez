<template>
  <PageLayout :title="form.id ? $t('recitations.edit') : $t('recitations.new')" icon="pi-book">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="emit('list')" />
    </template>

    <form class="grid grid-2" @submit.prevent="emit('submit')">
      <AppInput v-model="form.code"            :label="$t('recitations.code')"          required />
      <AppDatePicker v-model="form.recitationDate" :label="$t('recitations.recitationDate')"    />
      <AppSelect v-model="form.studentId"      :label="$t('recitations.student')">
        <option value="">{{ $t("common.select") }} {{ $t("recitations.student") }}</option>
        <option v-for="student in students" :key="student.id" :value="student.id">{{ student.name }}</option>
      </AppSelect>
      <div class="app-field">
        <label>{{ $t('recitations.numberOfAyat') }}</label>
        <input type="number" min="1" v-model.number="form.numberOfAyat" />
      </div>
      <AppSurahAyaPicker
        :label="$t('recitations.fromSurah')"
        :surah="form.fromSurah"
        :aya="form.fromAya"
        @update:surah="form.fromSurah = $event"
        @update:aya="form.fromAya = $event"
      />
      <AppSurahAyaPicker
        :label="$t('recitations.toSurah')"
        :surah="form.toSurah"
        :aya="form.toAya"
        @update:surah="form.toSurah = $event"
        @update:aya="form.toAya = $event"
      />
      <div class="app-field">
        <label>{{ $t('recitations.grade') }}</label>
        <input type="number" min="0" max="100" v-model.number="form.grade" />
      </div>
      <AppTextarea class="field-span-2" v-model="form.notes" :label="$t('recitations.notes')" rows="3" />

      <div class="field-span-2 mistake-section">
        <div class="mistake-section-header">
          <div>
            <strong>{{ $t('recitations.mistakeLog') }}</strong>
            <p class="small-muted">{{ $t('recitations.mistakeLogHelp') }}</p>
          </div>
          <Button type="button" :label="$t('recitations.addMistake')" icon="pi pi-plus" severity="secondary" @click="addMistake" />
        </div>
        <p v-if="!form.mistakes.length" class="notice">{{ $t('recitations.noMistakesLogged') }}</p>
        <div v-for="(mistake, index) in form.mistakes" :key="index" class="mistake-row">
          <AppMistakeTypeSelect
            :options="mistakeTypes"
            :placeholder="`${$t('common.select')} ${$t('mistakeTypes.mistakeType')}`"
            v-model="mistake.mistakeTypeId"
          />
          <input class="mistake-count" type="number" min="1" v-model.number="mistake.count" :placeholder="$t('recitations.count')" />
          <Button type="button" icon="pi pi-times" severity="danger" text rounded @click="form.mistakes.splice(index, 1)" />
        </div>
      </div>

      <div class="button-row">
        <Button type="submit" :label="form.id ? $t('common.save') : $t('common.create')" icon="pi pi-check" :loading="submitting" :disabled="submitting" />
        <Button type="button" :label="$t('common.cancel')" icon="pi pi-times" severity="secondary" :disabled="submitting" @click="emit('cancel')" />
      </div>
    </form>
  </PageLayout>
</template>

<script setup>
import Button from 'primevue/button';
import PageLayout from './PageLayout.vue';
import AppInput from './AppInput.vue';
import AppSelect from './AppSelect.vue';
import AppDatePicker from './AppDatePicker.vue';
import AppTextarea from './AppTextarea.vue';
import AppSurahAyaPicker from './AppSurahAyaPicker.vue';
import AppMistakeTypeSelect from './AppMistakeTypeSelect.vue';

const props = defineProps({
  form:         { type: Object,  required: true },
  students:     { type: Array,   default: () => [] },
  mistakeTypes: { type: Array,   default: () => [] },
  submitting:   { type: Boolean, default: false },
});

const emit = defineEmits(['submit', 'cancel', 'list']);

function addMistake() {
  props.form.mistakes.push({ mistakeTypeId: '', count: 1 });
}
</script>

<style scoped>
.mistake-section-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-4);
  margin-bottom: var(--space-3);
}

.mistake-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-2);
}

.mistake-row .app-field {
  flex: 1;
  margin-bottom: 0;
}

.mistake-count {
  width: 5rem;
  flex-shrink: 0;
}
</style>
