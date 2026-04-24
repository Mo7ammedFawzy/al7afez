<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("levels.list") }}</h2>
      </div>
      <button class="primary" type="button" @click="handleNew">{{ $t("common.new") }}</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("levels.name") }}</th>
          <th>{{ $t("levels.range") }}</th>
          <th>{{ $t("levels.ayatPerSessionShort") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="level in items" :key="level.id">
          <td>{{ level.name }}</td>
          <td>{{ level.fromSurah }}:{{ level.fromAya }} ? {{ level.toSurah }}:{{ level.toAya }}</td>
          <td>{{ level.numberOfAyatPerSession }}</td>
          <td>
            <div class="button-row">
              <button class="secondary" type="button" @click="handleEdit(level)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="handleRemove(level)">{{ $t("common.delete") }}</button>
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

function handleEdit(level) {
  emit("edit", level);
}

function handleRemove(level) {
  emit("remove", level);
}

function handlePrevPage() {
  emit("changePage", -1);
}

function handleNextPage() {
  emit("changePage", 1);
}
</script>
