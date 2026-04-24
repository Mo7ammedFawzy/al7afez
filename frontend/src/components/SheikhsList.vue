<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("sheikhs.list") }}</h2>
      </div>
      <button class="primary" type="button" @click="handleNew">{{ $t("common.new") }}</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("sheikhs.name") }}</th>
          <th>{{ $t("sheikhs.gender") }}</th>
          <th>{{ $t("sheikhs.phone") }}</th>
          <th>{{ $t("sheikhs.birthDate") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="sheikh in items" :key="sheikh.id">
          <td>{{ sheikh.name }}</td>
          <td>{{ sheikh.gender === "MALE" ? $t("common.male") : $t("common.female") }}</td>
          <td>{{ sheikh.phoneNumber }}</td>
          <td>{{ sheikh.birthDate }}</td>
          <td>
            <div class="button-row">
              <button class="secondary" type="button" @click="handleEdit(sheikh)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="handleRemove(sheikh)">{{ $t("common.delete") }}</button>
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

function handleEdit(sheikh) {
  emit("edit", sheikh);
}

function handleRemove(sheikh) {
  emit("remove", sheikh);
}

function handlePrevPage() {
  emit("changePage", -1);
}

function handleNextPage() {
  emit("changePage", 1);
}
</script>
