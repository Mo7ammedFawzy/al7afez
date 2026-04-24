<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("mistakeTypes.list") }}</h2>
      </div>
      <button class="primary" type="button" @click="handleNew">{{ $t("common.new") }}</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("mistakeTypes.name") }}</th>
          <th>{{ $t("mistakeTypes.code") }}</th>
          <th>{{ $t("mistakeTypes.parentType") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="type in items" :key="type.id">
          <td>{{ type.name }}</td>
          <td>{{ type.code || "-" }}</td>
          <td>{{ type.parent?.name || $t("mistakeTypes.noParent") }}</td>
          <td>
            <div class="button-row">
              <button class="secondary" type="button" @click="handleEdit(type)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="handleRemove(type)">{{ $t("common.delete") }}</button>
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

function handleEdit(type) {
  emit("edit", type);
}

function handleRemove(type) {
  emit("remove", type);
}

function handlePrevPage() {
  emit("changePage", -1);
}

function handleNextPage() {
  emit("changePage", 1);
}
</script>
