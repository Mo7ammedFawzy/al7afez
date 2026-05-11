<template>
  <PageLayout :title="form.id ? $t('groups.edit') : $t('groups.new')" icon="pi-sitemap">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="emit('list')" />
    </template>

    <form class="grid grid-2" @submit.prevent="emit('submit')">
      <AppInput v-model="form.name" :label="$t('groups.name')" required />
      <AppInput v-model="form.code" :label="$t('groups.code')"          />
      <AppSelect v-model="form.levelId" :label="$t('groups.level')">
        <option value="">{{ $t("groups.selectLevel") }}</option>
        <option v-for="level in levels" :key="level.id" :value="level.id">{{ level.name }}</option>
      </AppSelect>
      <AppSelect v-model="form.sheikhId" :label="$t('groups.sheikh')">
        <option value="">{{ $t("groups.selectSheikh") }}</option>
        <option v-for="sheikh in sheikhs" :key="sheikh.id" :value="sheikh.id">{{ sheikh.name }}</option>
      </AppSelect>
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
import AppSelect from './AppSelect.vue';

defineProps({
  form:    { type: Object, required: true },
  levels:  { type: Array,  default: () => [] },
  sheikhs: { type: Array,  default: () => [] },
});

const emit = defineEmits(['submit', 'cancel', 'list']);
</script>
