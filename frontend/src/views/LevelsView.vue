<template>
  <section class="card">
    <h2>{{ $t("levels.title") }}</h2>
    <p class="badge">{{ $t("badge.master") }}</p>
    <div v-if="error" class="notice">{{ error }}</div>
  </section>

  <section class="card">
    <h2>{{ form.id ? $t("levels.edit") : $t("levels.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="submit">
      <div>
        <label>{{ $t("levels.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("levels.code") }}</label>
        <input v-model="form.code" />
      </div>
      <div>
        <label>{{ $t("levels.fromSurah") }}</label>
        <input v-model.number="form.fromSurah" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("levels.toSurah") }}</label>
        <input v-model.number="form.toSurah" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("levels.fromAya") }}</label>
        <input v-model.number="form.fromAya" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("levels.toAya") }}</label>
        <input v-model.number="form.toAya" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("levels.ayatPerSession") }}</label>
        <input v-model.number="form.numberOfAyatPerSession" type="number" min="1" />
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="reset">{{ $t("common.clear") }}</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>{{ $t("levels.list") }}</h2>
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
              <button class="secondary" type="button" @click="edit(level)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="remove(level)">{{ $t("common.delete") }}</button>
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
const error = ref("");
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
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
}

function reset() {
  form.value = emptyForm();
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
    await load();
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
