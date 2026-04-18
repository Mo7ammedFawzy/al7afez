<template>
  <section class="card">
    <h2>{{ $t("recitations.title") }}</h2>
    <p class="badge">{{ $t("recitations.subtitle") }}</p>
    <div v-if="error" class="notice">{{ error }}</div>
  </section>

  <section class="card">
    <h2>{{ form.id ? $t("recitations.edit") : $t("recitations.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="submit">
      <div>
        <label>{{ $t("recitations.code") }}</label>
        <input v-model="form.code" />
      </div>
      <div>
        <label>{{ $t("recitations.recitationDate") }}</label>
        <input v-model="form.recitationDate" type="date" />
      </div>
      <div>
        <label>{{ $t("recitations.student") }}</label>
        <select v-model="form.studentId">
          <option value="">{{ $t("common.select") }} {{ $t("recitations.student") }}</option>
          <option v-for="student in students" :key="student.id" :value="student.id">
            {{ student.name }}
          </option>
        </select>
      </div>
      <div>
        <label>{{ $t("recitations.numberOfAyat") }}</label>
        <input v-model.number="form.numberOfAyat" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.fromSurah") }}</label>
        <input v-model.number="form.fromSurah" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.toSurah") }}</label>
        <input v-model.number="form.toSurah" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.fromAya") }}</label>
        <input v-model.number="form.fromAya" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.toAya") }}</label>
        <input v-model.number="form.toAya" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.grade") }}</label>
        <input v-model.number="form.grade" type="number" min="0" max="100" />
      </div>
      <div class="field-span-2">
        <label>{{ $t("recitations.notes") }}</label>
        <textarea v-model="form.notes" rows="3" />
      </div>
      <div class="field-span-2">
        <div class="section-header">
          <div>
            <strong>{{ $t("recitations.mistakeLog") }}</strong>
            <p class="small-muted">{{ $t("recitations.mistakeLogHelp") }}</p>
          </div>
          <button class="secondary" type="button" @click="addMistake">{{ $t("recitations.addMistake") }}</button>
        </div>
        <div v-if="form.mistakes.length === 0" class="notice">
          {{ $t("recitations.noMistakesLogged") }}
        </div>
        <div v-for="(mistake, index) in form.mistakes" :key="index" class="mistake-row">
          <select v-model="mistake.mistakeTypeId">
            <option value="">{{ $t("common.select") }} {{ $t("mistakeTypes.mistakeType") }}</option>
            <option v-for="type in mistakeTypes" :key="type.id" :value="type.id">
              {{ type.name }}
            </option>
          </select>
          <input v-model.number="mistake.count" type="number" min="1" :placeholder="$t('recitations.count')" />
          <input v-model="mistake.note" :placeholder="$t('recitations.mistakeNote')" />
          <button class="danger" type="button" @click="removeMistake(index)">{{ $t("common.delete") }}</button>
        </div>
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="reset">{{ $t("common.clear") }}</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>{{ $t("recitations.list") }}</h2>
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
              <button class="secondary" type="button" @click="edit(recitation)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="remove(recitation)">{{ $t("common.delete") }}</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="pager">
      <span>{{ page + 1 }} / {{ totalPages || 1 }}</span>
      <button class="secondary" type="button" :disabled="page === 0" @click="changePage(-1)">{{ $t("common.prev") }}</button>
      <button class="secondary" type="button" :disabled="page + 1 >= totalPages" @click="changePage(1)">{{ $t("common.next") }}</button>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";

const items = ref([]);
const students = ref([]);
const mistakeTypes = ref([]);
const error = ref("");
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const pageSize = 10;

function emptyForm() {
  return {
    id: null,
    code: "",
    recitationDate: "",
    studentId: "",
    fromSurah: null,
    toSurah: null,
    fromAya: null,
    toAya: null,
    numberOfAyat: null,
    grade: null,
    notes: "",
    mistakes: []
  };
}

async function load() {
  try {
    error.value = "";
    const [recitationsData, studentsData, mistakeTypesData] = await Promise.all([
      apiGet("/recitations", { page: page.value, size: pageSize }),
      apiGet("/students", { page: 0, size: 100 }),
      apiGet("/mistake-types", { page: 0, size: 100 })
    ]);
    items.value = recitationsData.content ?? recitationsData;
    totalPages.value = recitationsData.totalPages ?? 1;
    students.value = studentsData.content ?? studentsData;
    mistakeTypes.value = mistakeTypesData.content ?? mistakeTypesData;
  } catch (err) {
    error.value = err.message;
  }
}

function edit(recitation) {
  form.value = {
    id: recitation.id,
    code: recitation.code || "",
    recitationDate: recitation.recitationDate || "",
    studentId: recitation.student?.id || "",
    fromSurah: recitation.fromSurah ?? null,
    toSurah: recitation.toSurah ?? null,
    fromAya: recitation.fromAya ?? null,
    toAya: recitation.toAya ?? null,
    numberOfAyat: recitation.numberOfAyat ?? null,
    grade: recitation.grade ?? null,
    notes: recitation.notes || "",
    mistakes: (recitation.mistakes || []).map((mistake) => ({
      mistakeTypeId: mistake.mistakeType?.id || "",
      count: mistake.count ?? 1,
      note: mistake.note || ""
    }))
  };
}

function reset() {
  form.value = emptyForm();
}

function buildPayload() {
  return {
    code: form.value.code || null,
    recitationDate: form.value.recitationDate || null,
    studentId: form.value.studentId ? Number(form.value.studentId) : null,
    fromSurah: form.value.fromSurah ?? null,
    toSurah: form.value.toSurah ?? null,
    fromAya: form.value.fromAya ?? null,
    toAya: form.value.toAya ?? null,
    numberOfAyat: form.value.numberOfAyat ?? null,
    grade: form.value.grade ?? null,
    notes: form.value.notes || null,
    mistakes: form.value.mistakes
      .filter((mistake) => mistake.mistakeTypeId && mistake.count)
      .map((mistake) => ({
        mistakeTypeId: Number(mistake.mistakeTypeId),
        count: Number(mistake.count),
        note: mistake.note || null
      }))
  };
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

function addMistake() {
  form.value.mistakes.push({
    mistakeTypeId: "",
    count: 1,
    note: ""
  });
}

function removeMistake(index) {
  form.value.mistakes.splice(index, 1);
}

async function submit() {
  try {
    error.value = "";
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/recitations/${form.value.id}`, payload);
    } else {
      await apiPost("/recitations", payload);
      page.value = 0;
    }
    reset();
    await load();
  } catch (err) {
    error.value = err.message;
  }
}

async function remove(recitation) {
  if (!recitation?.id) {
    return;
  }
  try {
    error.value = "";
    await apiDelete(`/recitations/${recitation.id}`);
    await load();
    if (items.value.length === 0 && page.value > 0) {
      page.value -= 1;
      await load();
    }
  } catch (err) {
    error.value = err.message;
  }
}

function changePage(delta) {
  const next = page.value + delta;
  if (next < 0 || next >= totalPages.value) return;
  page.value = next;
  load();
}

onMounted(load);
</script>
