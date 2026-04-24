<template>
  <section class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
      <h2 style="margin: 0;">{{ $t("groups.list") }}</h2>
      <button class="primary" @click="handleNew">{{ $t("common.new") }}</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("groups.name") }}</th>
          <th>{{ $t("groups.level") }}</th>
          <th>{{ $t("groups.sheikh") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="group in items" :key="group.id">
          <td>{{ group.name }}</td>
          <td>{{ group.level?.name || "" }}</td>
          <td>{{ group.sheikh?.name || "" }}</td>
          <td>
            <div class="button-row">
              <button class="secondary" type="button" @click="handleEdit(group)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="handleRemove(group)">{{ $t("common.delete") }}</button>
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
  totalPages: {
    type: Number,
    default: 1
  },
  page: {
    type: Number,
    default: 0
  }
});

const emit = defineEmits(["edit", "remove", "changePage", "new"]);

function handleNew() {
  emit("new");
}

function handleEdit(group) {
  emit("edit", group);
}

function handleRemove(group) {
  emit("remove", group);
}

function handlePrevPage() {
  emit("changePage", -1);
}

function handleNextPage() {
  emit("changePage", 1);
}
</script>
