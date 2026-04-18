<template>
  <section class="card">
    <h2>{{ $t("mistakeTypes.title") }}</h2>
    <p class="badge">{{ $t("badge.master") }}</p>
    <div v-if="error" class="notice">{{ error }}</div>
  </section>

  <section class="card">
    <h2>{{ form.id ? $t("mistakeTypes.edit") : $t("mistakeTypes.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="submit">
      <div>
        <label>{{ $t("mistakeTypes.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("mistakeTypes.code") }}</label>
        <input v-model="form.code" />
      </div>
      <div>
        <label>{{ $t("mistakeTypes.mistakeType") }}</label>
        <input v-model="form.mistakeType" />
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="reset">{{ $t("common.clear") }}</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>{{ $t("mistakeTypes.list") }}</h2>
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("mistakeTypes.name") }}</th>
          <th>{{ $t("mistakeTypes.mistakeType") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="type in items" :key="type.id">
          <td>{{ type.name }}</td>
          <td>{{ type.mistakeType }}</td>
          <td>
            <div class="button-row">
              <button class="secondary" type="button" @click="edit(type)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="remove(type)">{{ $t("common.delete") }}</button>
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
    mistakeType: ""
  };
}

async function load() {
  try {
    error.value = "";
    const data = await apiGet("/mistake-types", { page: page.value, size: pageSize });
    items.value = data.content ?? data;
    totalPages.value = data.totalPages ?? 1;
  } catch (err) {
    error.value = err.message;
  }
}

function edit(type) {
  form.value = {
    id: type.id,
    name: type.name || "",
    code: type.code || "",
    mistakeType: type.mistakeType || ""
  };
}

function reset() {
  form.value = emptyForm();
}

function buildPayload() {
  return {
    name: form.value.name,
    code: form.value.code || null,
    mistakeType: form.value.mistakeType || null
  };
}

async function submit() {
  try {
    error.value = "";
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/mistake-types/${form.value.id}`, payload);
    } else {
      await apiPost("/mistake-types", payload);
      page.value = 0;
    }
    await load();
  } catch (err) {
    error.value = err.message;
  }
}

async function remove(type) {
  if (!type?.id) {
    return;
  }
  try {
    error.value = "";
    await apiDelete(`/mistake-types/${type.id}`);
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
