<template>
  <PageLayout :title="form.id ? $t('levels.edit') : $t('levels.new')" icon="pi-list">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="emit('list')" />
    </template>

    <form class="grid grid-2" @submit.prevent="emit('submit')">
      <AppInput v-model="form.name" :label="$t('levels.name')" required />
      <AppInput v-model="form.code" :label="$t('levels.code')"          />
      <AppSurahAyaPicker
        :label="$t('levels.fromSurah')"
        :surah="form.fromSurah"
        :aya="form.fromAya"
        @update:surah="form.fromSurah = $event"
        @update:aya="form.fromAya = $event"
      />
      <AppSurahAyaPicker
        :label="$t('levels.toSurah')"
        :surah="form.toSurah"
        :aya="form.toAya"
        @update:surah="form.toSurah = $event"
        @update:aya="form.toAya = $event"
      />
      <div class="app-field">
        <label>{{ $t('levels.ayatPerSession') }}</label>
        <input type="number" min="1" v-model.number="form.numberOfAyatPerSession" />
      </div>
      <div class="button-row">
        <Button type="submit" :label="form.id ? $t('common.save') : $t('common.create')" icon="pi pi-check" />
        <Button type="button" :label="$t('common.cancel')" icon="pi pi-times" severity="secondary" @click="emit('cancel')" />
      </div>
    </form>
  </PageLayout>
</template>

<script setup>
import Button from 'primevue/button';
import PageLayout from './PageLayout.vue';
import AppInput from './AppInput.vue';
import AppSurahAyaPicker from './AppSurahAyaPicker.vue';

defineProps({
  form: { type: Object, required: true },
});

const emit = defineEmits(['submit', 'cancel', 'list']);
</script>
