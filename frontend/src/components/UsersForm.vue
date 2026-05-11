<template>
  <PageLayout :title="form.id ? $t('users.edit') : $t('users.new')" icon="pi-user">
    <template #actions>
      <Button :label="$t('common.list')" icon="pi pi-list" severity="secondary" @click="emit('list')" />
    </template>

    <form class="grid grid-2" @submit.prevent="emit('submit')">
      <AppInput v-model="form.name"     :label="$t('users.name')"     required />
      <AppInput v-model="form.code"     :label="$t('users.code')"              />
      <AppInput v-model="form.username" :label="$t('users.username')" required />
      <AppSelect v-model="form.sheikhId" :label="$t('users.sheikh')">
        <option value="">{{ $t("users.selectSheikh") }}</option>
        <option v-for="sheikh in sheikhs" :key="sheikh.id" :value="sheikh.id">{{ sheikh.name }}</option>
      </AppSelect>
      <AppInput
        v-if="!form.id"
        v-model="form.password"
        :label="$t('users.password')"
        type="password"
        required
      />
      <div class="button-row">
        <Button type="submit" :label="form.id ? $t('common.save') : $t('common.create')" icon="pi pi-check" :loading="submitting" :disabled="submitting" />
        <Button type="button" :label="$t('common.cancel')" icon="pi pi-times" severity="secondary" :disabled="submitting" @click="emit('cancel')" />
      </div>
    </form>

    <template v-if="form.id">
      <hr class="section-divider" />
      <form class="grid grid-2" @submit.prevent="submitChangePassword">
        <AppInput v-model="newPassword" :label="$t('users.newPassword')" type="password" required />
        <div class="button-row">
          <Button type="submit" :label="$t('users.changePassword')" icon="pi pi-lock" severity="secondary" :loading="submitting" :disabled="submitting" />
        </div>
      </form>
    </template>
  </PageLayout>
</template>

<script setup>
import { ref } from 'vue';
import Button from 'primevue/button';
import PageLayout from './PageLayout.vue';
import AppInput from './AppInput.vue';
import AppSelect from './AppSelect.vue';

defineProps({
  form:       { type: Object,  required: true },
  sheikhs:    { type: Array,   default: () => [] },
  submitting: { type: Boolean, default: false },
});

const emit = defineEmits(['submit', 'cancel', 'list', 'changePassword']);

const newPassword = ref('');

function submitChangePassword() {
  emit('changePassword', newPassword.value);
  newPassword.value = '';
}
</script>

<style scoped>
.section-divider {
  border: none;
  border-top: 1px solid rgba(27, 60, 116, 0.1);
  margin: 0;
}
</style>
