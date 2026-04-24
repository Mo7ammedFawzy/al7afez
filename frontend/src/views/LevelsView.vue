<template>
  <div v-if="error" class="popup-overlay" @click.self="error.value = ''">
    <div class="popup-card">
      <p>{{ error }}</p>
      <button class="secondary" type="button" @click="error.value = ''">{{ $t("common.cancel") }}</button>
    </div>
  </div>

  <LevelsForm
    v-if="showForm"
    :form="form"
    @submit="submit"
    @cancel="cancelEdit"
  />

  <LevelsList
    v-else
    :items="items"
    :page="page"
    :totalPages="totalPages"
    @new="newLevel"
    @edit="edit"
    @remove="remove"
    @changePage="changePage"
  />
</template>

<script setup>
import { onMounted, ref } from "vue";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";
import LevelsForm from "../components/LevelsForm.vue";
import LevelsList from "../components/LevelsList.vue";

const items = ref([]);
const error = ref("");
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const showForm = ref(false);
const pageSize = 10;

function emptyForm() {
  return {
    id: null,
    name: "",
    code: "",
    fromSurah: null,
    toSurah: null,
    fromAya: null,
    toAya: null,
    numberOfAyatPerSession: null
  };
}

async function load() {
  try {
    error.value = "";
    const data = await apiGet("/levels", { page: page.value, size: pageSize });
    items.value = data.content ?? data;
    totalPages.value = data.totalPages ?? 1;
  } catch (err) {
    error.value = err.message;
  }
}

function newLevel() {
  form.value = emptyForm();
  showForm.value = true;
}

function edit(level) {
  form.value = {
    id: level.id,
    name: level.name || "",
    code: level.code || "",
    fromSurah: level.fromSurah ?? null,
    toSurah: level.toSurah ?? null,
    fromAya: level.fromAya ?? null,
    toAya: level.toAya ?? null,
    numberOfAyatPerSession: level.numberOfAyatPerSession ?? null
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
    name: form.value.name,
    code: form.value.code || null,
    fromSurah: form.value.fromSurah ?? 0,
    toSurah: form.value.toSurah ?? 0,
    fromAya: form.value.fromAya ?? 0,
    toAya: form.value.toAya ?? 0,
    numberOfAyatPerSession: form.value.numberOfAyatPerSession ?? 0
  };
}

async function submit() {
  try {
    error.value = "";
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/levels/${form.value.id}`, payload);
    } else {
      await apiPost("/levels", payload);
      page.value = 0;
    }
    form.value = emptyForm();
    showForm.value = true;
  } catch (err) {
    error.value = err.message;
  }
}

async function remove(level) {
  if (!level?.id) {
    return;
  }
  try {
    error.value = "";
    await apiDelete(`/levels/${level.id}`);
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
