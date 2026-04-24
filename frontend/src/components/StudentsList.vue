<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("students.list") }}</h2>
      </div>
      <button class="primary" type="button" @click="handleNew">{{ $t("common.new") }}</button>
    </div>
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
          <td>{{ student.level?.name || "-" }}</td>
          <td>{{ student.gender === "MALE" ? $t("common.male") : $t("common.female") }}</td>
          <td>{{ student.phoneNumber }}</td>
          <td>{{ student.parentPhoneNumber }}</td>
          <td>{{ student.birthDate }}</td>
          <td>
            <div class="button-row">
              <button class="secondary" type="button" @click="handleEdit(student)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="handleRemove(student)">{{ $t("common.delete") }}</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="pager">
      <span>{{ page + 1 }} / {{ totalPages || 1 }}</span>
      <button class="secondary" type="button" :disabled="page === 0" @click="handlePrevPage">{{ $t("common.prev") }}</button>
      <button class="secondary" type="button" :disabled="page + 1 >= totalPages" @click="handleNextPage">{{ $t("common.next") }}</button>
    </div>
  </section>
</template>

<script setup>
defineProps({
  items: {
    type: Array,
    default: () => []
  },
  page: {
    type: Number,
    default: 0
  },
  totalPages: {
    type: Number,
    default: 1
  }
});

const emit = defineEmits(["edit", "remove", "changePage", "new"]);

function handleNew() {
  emit("new");
}

function handleEdit(student) {
  emit("edit", student);
}

function handleRemove(student) {
  emit("remove", student);
}

function handlePrevPage() {
  emit("changePage", -1);
}

function handleNextPage() {
  emit("changePage", 1);
}
</script>
