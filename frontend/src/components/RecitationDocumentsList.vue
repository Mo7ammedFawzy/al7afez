<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("recitations.list") }}</h2>
      </div>
      <button class="primary icon" type="button" @click="handleNew" :title="$t('common.new')" :aria-label="$t('common.new')">＋</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("recitations.recitationDate") }}</th>
          <th>{{ $t("recitations.student") }}</th>
          <th>{{ $t("groups.group") }}</th>
          <th>{{ $t("recitations.range") }}</th>
          <th>{{ $t("recitations.ayat") }}</th>
          <th>{{ $t("recitations.grade") }}</th>
          <th>{{ $t("recitations.totalMistakes") }}</th>
          <th>{{ $t("common.mistakes") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="recitation in items" :key="recitation.id">
          <td>{{ recitation.recitationDate || "-" }}</td>
          <td>{{ recitation.student?.name || "-" }}</td>
          <td>{{ recitation.group?.name || "-" }}</td>
          <td>{{ rangeLabel(recitation) }}</td>
          <td>{{ recitation.numberOfAyat }}</td>
          <td>{{ recitation.grade }}</td>
          <td>{{ recitation.totalMistakes }}</td>
          <td>{{ joinNames(recitation.mistakes) }}</td>
          <td>
            <div class="button-row">
              <button class="secondary icon" type="button" @click="handleEdit(recitation)" :title="$t('common.edit')" :aria-label="$t('common.edit')">✏️</button>
              <button class="danger icon" type="button" @click="handleRemove(recitation)" :title="$t('common.delete')" :aria-label="$t('common.delete')">🗑️</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="pager">
      <button class="secondary icon" type="button" :disabled="page === 0" @click="handlePrevPage" :title="$t('common.prev')" :aria-label="$t('common.prev')">▶</button>
      <span>{{ page + 1 }} / {{ totalPages || 1 }}</span>
      <button class="secondary icon" type="button" :disabled="page + 1 >= totalPages" @click="handleNextPage" :title="$t('common.next')" :aria-label="$t('common.next')">◀</button>
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

function handleEdit(recitation) {
  emit("edit", recitation);
}

function handleRemove(recitation) {
  emit("remove", recitation);
}

function handlePrevPage() {
  emit("changePage", -1);
}

function handleNextPage() {
  emit("changePage", 1);
}

function joinNames(selected) {
  if (!selected || selected.length === 0) {
    return "";
  }
  return selected
    .map((item) => `${item.mistakeType?.name || ""} (${item.count})`)
    .filter(Boolean)
    .join(", ");
}

function rangeLabel(recitation) {
  const start = recitation.fromSurah && recitation.fromAya ? `${recitation.fromSurah}:${recitation.fromAya}` : "-";
  const end = recitation.toSurah && recitation.toAya ? `${recitation.toSurah}:${recitation.toAya}` : "-";
  return `${start} - ${end}`;
}
</script>
