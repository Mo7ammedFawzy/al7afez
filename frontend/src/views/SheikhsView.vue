<template>
  <section class="card">
    <h2>{{ $t("sheikhs.title") }}</h2>
    <p class="badge">{{ $t("badge.master") }}</p>
    <div v-if="error" class="notice">{{ error }}</div>
  </section>

  <section class="card">
    <h2>{{ form.id ? $t("sheikhs.edit") : $t("sheikhs.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="submit">
      <div>
        <label>{{ $t("sheikhs.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("sheikhs.code") }}</label>
        <input v-model="form.code" />
      </div>
      <div>
        <label>{{ $t("sheikhs.birthDate") }}</label>
        <input v-model="form.birthDate" type="date" />
      </div>
      <div>
        <label>{{ $t("sheikhs.gender") }}</label>
        <select v-model="form.gender">
          <option value="MALE">{{ $t("common.male") }}</option>
          <option value="FEMALE">{{ $t("common.female") }}</option>
        </select>
      </div>
      <div>
        <label>{{ $t("sheikhs.phone") }}</label>
        <input v-model="form.phoneNumber" />
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="reset">{{ $t("common.clear") }}</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>{{ $t("sheikhs.list") }}</h2>
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
              <button class="secondary" type="button" @click="edit(sheikh)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="remove(sheikh)">{{ $t("common.delete") }}</button>
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
    birthDate: "",
    phoneNumber: "",
    gender: "MALE"
  };
}

async function load() {
  try {
    error.value = "";
    const data = await apiGet("/sheikhs", { page: page.value, size: pageSize });
    items.value = data.content ?? data;
    totalPages.value = data.totalPages ?? 1;
  } catch (err) {
    error.value = err.message;
  }
}

function edit(sheikh) {
  form.value = {
    id: sheikh.id,
    name: sheikh.name || "",
    code: sheikh.code || "",
    birthDate: sheikh.birthDate || "",
    phoneNumber: sheikh.phoneNumber || "",
    gender: sheikh.gender || "MALE"
  };
}

function reset() {
  form.value = emptyForm();
}

function buildPayload() {
  return {
    name: form.value.name,
    code: form.value.code || null,
    birthDate: form.value.birthDate || null,
    phoneNumber: form.value.phoneNumber || null,
    gender: form.value.gender || null
  };
}

async function submit() {
  try {
    error.value = "";
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/sheikhs/${form.value.id}`, payload);
    } else {
      await apiPost("/sheikhs", payload);
      page.value = 0;
    }
    await load();
  } catch (err) {
    error.value = err.message;
  }
}

async function remove(sheikh) {
  if (!sheikh?.id) {
    return;
  }
  try {
    error.value = "";
    await apiDelete(`/sheikhs/${sheikh.id}`);
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
