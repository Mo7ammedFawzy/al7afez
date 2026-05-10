<template>
  <PageLayout :title="$t('students.list')" icon="pi-users" :count="items.length">
    <template #actions>
      <Button
        :label="$t('students.new')"
        icon="pi pi-plus"
        @click="emit('new')"
      />
    </template>

    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("students.name") }}</th>
          <th>{{ $t("students.group") }}</th>
          <th>{{ $t("students.level") }}</th>
          <th>{{ $t("students.gender") }}</th>
          <th>{{ $t("students.phone") }}</th>
          <th>{{ $t("students.parentPhone") }}</th>
          <th>{{ $t("students.birthDate") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="student in items" :key="student.id">
          <td>{{ student.name }}</td>
          <td>{{ student.recitationGroup?.name || $t("students.noGroup") }}</td>
          <td>{{ student.level?.name || "—" }}</td>
          <td>{{ student.gender === "MALE" ? $t("common.male") : $t("common.female") }}</td>
          <td class="ltr">{{ student.phoneNumber }}</td>
          <td class="ltr">{{ student.parentPhoneNumber }}</td>
          <td class="ltr">{{ student.birthDate }}</td>
          <td>
            <div class="row-actions">
              <Button
                icon="pi pi-pencil"
                severity="secondary"
                text
                rounded
                :title="$t('common.edit')"
                @click="emit('edit', student)"
              />
              <Button
                icon="pi pi-trash"
                severity="danger"
                text
                rounded
                :title="$t('common.delete')"
                @click="emit('remove', student)"
              />
            </div>
          </td>
        </tr>
        <tr v-if="!items.length">
          <td colspan="8" class="empty-row">{{ $t("students.list") }} — لا توجد بيانات</td>
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
  </PageLayout>
</template>

<script setup>
import Button from "primevue/button";
import PageLayout from "./PageLayout.vue";

defineProps({
  items:      { type: Array,  default: () => [] },
  page:       { type: Number, default: 0 },
  totalPages: { type: Number, default: 1 },
});

const emit = defineEmits(["edit", "remove", "changePage", "new"]);
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
