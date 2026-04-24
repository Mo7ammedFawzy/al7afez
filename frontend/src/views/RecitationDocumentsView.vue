<template>
  <div v-if="error" class="popup-overlay" @click.self="error.value = ''">
    <div class="popup-card">
      <p>{{ error }}</p>
      <button class="secondary" type="button" @click="error.value = ''">{{ $t("common.cancel") }}</button>
    </div>
  </div>

  <RecitationDocumentsForm
    v-if="showForm"
    :form="form"
    :students="students"
    :mistakeTypes="mistakeTypes"
    @submit="submit"
    @cancel="cancelEdit"
    @list="showListView"
  />

  <RecitationDocumentsList
    v-else
    :items="items"
    :page="page"
    :totalPages="totalPages"
    @new="newRecitation"
    @edit="edit"
    @remove="remove"
    @changePage="changePage"
  />
</template>

<script setup>
import { onMounted, ref } from "vue";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";
import RecitationDocumentsForm from "../components/RecitationDocumentsForm.vue";
import RecitationDocumentsList from "../components/RecitationDocumentsList.vue";

const items = ref([]);
const students = ref([]);
const mistakeTypes = ref([]);
const error = ref("");
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const showForm = ref(false);
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

function newRecitation() {
  form.value = emptyForm();
  showForm.value = true;
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
  showForm.value = true;
}

function cancelEdit() {
  form.value = emptyForm();
  showForm.value = false;
}

function showListView() {
  form.value = emptyForm();
  showForm.value = false;
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
    form.value = emptyForm();
    showForm.value = true;
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
