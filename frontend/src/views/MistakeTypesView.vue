<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("mistakeTypes.title") }}</h2>
        <p class="small-muted">{{ $t("mistakeTypes.subtitle") }}</p>
      </div>
      <button class="secondary" type="button" @click="startCreate">{{ $t("mistakeTypes.new") }}</button>
    </div>
    <div v-if="error" class="notice">{{ error }}</div>
  </section>

  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("mistakeTypes.list") }}</h2>
        <p class="small-muted">{{ $t("mistakeTypes.listHelp") }}</p>
      </div>
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
        <tr v-for="type in items" :key="type.id" :class="{ 'table-row-active': form.id === type.id }">
          <td>{{ type.name }}</td>
          <td>{{ type.code || "-" }}</td>
          <td>{{ type.parent?.name || $t("mistakeTypes.noParent") }}</td>
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

  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ isEditing ? $t("mistakeTypes.edit") : $t("mistakeTypes.new") }}</h2>
        <p class="small-muted">
          {{ isEditing ? $t("mistakeTypes.editHelp") : $t("mistakeTypes.createHelp") }}
        </p>
      </div>
      <button v-if="isEditing" class="secondary" type="button" @click="startCreate">{{ $t("mistakeTypes.cancelEdit") }}</button>
    </div>

    <form class="grid grid-2" @submit.prevent="submit">
      <div>
        <label>{{ $t("mistakeTypes.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("mistakeTypes.code") }}</label>
        <input v-model="form.code" />
      </div>
      <div class="field-span-2">
        <label>{{ $t("mistakeTypes.parentType") }}</label>
        <select v-model="form.parentId">
          <option value="">{{ $t("mistakeTypes.noParent") }}</option>
          <option v-for="option in parentOptions" :key="option.id" :value="option.id">
            {{ option.name }}
          </option>
        </select>
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ isEditing ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="reset">{{ $t("common.clear") }}</button>
      </div>
    </form>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";

const { t } = useI18n();
const items = ref([]);
const allTypes = ref([]);
const error = ref("");
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const pageSize = 10;

const isEditing = computed(() => Boolean(form.value.id));
const parentOptions = computed(() => allTypes.value.filter((item) => item.id !== form.value.id));

function emptyForm() {
  return {
    id: null,
    name: "",
    code: "",
    parentId: ""
  };
}

async function load() {
  try {
    error.value = "";
    const [pagedData, allData] = await Promise.all([
      apiGet("/mistake-types", { page: page.value, size: pageSize }),
      apiGet("/mistake-types", { page: 0, size: 200 })
    ]);
    items.value = pagedData.content ?? pagedData;
    totalPages.value = pagedData.totalPages ?? 1;
    allTypes.value = allData.content ?? allData;
  } catch (err) {
    error.value = err.message;
  }
}

function edit(type) {
  form.value = {
    id: type.id,
    name: type.name || "",
    code: type.code || "",
    parentId: type.parent?.id || ""
  };
}

function startCreate() {
  reset();
}

function reset() {
  form.value = emptyForm();
}

function buildPayload() {
  return {
    name: form.value.name,
    code: form.value.code || null,
    parentId: form.value.parentId ? Number(form.value.parentId) : null
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
    reset();
    await load();
  } catch (err) {
    error.value = err.message;
  }
}

async function remove(type) {
  if (!type?.id) {
    return;
  }
  const confirmed = confirm(t("mistakeTypes.deleteConfirm", { name: type.name }));
  if (!confirmed) {
    return;
  }
  try {
    error.value = "";
    await apiDelete(`/mistake-types/${type.id}`);
    if (form.value.id === type.id) {
      reset();
    }
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
