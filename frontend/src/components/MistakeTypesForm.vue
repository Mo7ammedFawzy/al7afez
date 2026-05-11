<template>
  <PageLayout :title="form.id ? $t('mistakeTypes.edit') : $t('mistakeTypes.new')" icon="pi-exclamation-triangle">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="emit('list')" />
    </template>

    <form class="grid grid-2" @submit.prevent="emit('submit')">
      <AppInput v-model="form.code" :label="$t('mistakeTypes.code')"   required=""       />
      <AppInput v-model="form.name" :label="$t('mistakeTypes.name')" required />
      <AppMistakeTypeSelect
        class="field-span-2"
        :label="$t('mistakeTypes.parentType')"
        :options="parentOptions"
        :placeholder="$t('mistakeTypes.noParent')"
        v-model="form.parentId"
      />
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
import AppMistakeTypeSelect from './AppMistakeTypeSelect.vue';

defineProps({
  form:          { type: Object, required: true },
  parentOptions: { type: Array,  default: () => [] },
});

const emit = defineEmits(['submit', 'cancel', 'list']);
</script>
