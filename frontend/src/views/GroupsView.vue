<template>
  <section class="card">
    <h2>{{ $t("groups.title") }}</h2>
    <p class="badge">{{ $t("groups.subtitle") }}</p>
    <div v-if="error" class="notice">{{ error }}</div>
  </section>

  <section class="card">
    <h2>{{ form.id ? $t("groups.edit") : $t("groups.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="submit">
      <div>
        <label>{{ $t("groups.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("groups.code") }}</label>
        <input v-model="form.code" />
      </div>
      <div>
        <label>{{ $t("groups.level") }}</label>
        <select v-model="form.levelId">
          <option value="">{{ $t("groups.selectLevel") }}</option>
          <option v-for="level in levels" :key="level.id" :value="level.id">
            {{ level.name }}
          </option>
        </select>
      </div>
      <div>
        <label>{{ $t("groups.sheikh") }}</label>
        <select v-model="form.sheikhId">
          <option value="">{{ $t("groups.selectSheikh") }}</option>
          <option v-for="sheikh in sheikhs" :key="sheikh.id" :value="sheikh.id">
            {{ sheikh.name }}
          </option>
        </select>
      </div>
      <div class="field-span-2">
        <label>{{ $t("groups.students") }}</label>
        <select v-model="form.studentIds" multiple>
          <option v-for="student in students" :key="student.id" :value="student.id">
            {{ student.name }}
          </option>
        </select>
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="reset">{{ $t("common.clear") }}</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>{{ $t("groups.list") }}</h2>
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("groups.name") }}</th>
          <th>{{ $t("groups.level") }}</th>
          <th>{{ $t("groups.sheikh") }}</th>
          <th>{{ $t("groups.studentCount") }}</th>
          <th>{{ $t("groups.students") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="group in items" :key="group.id">
          <td>{{ group.name }}</td>
          <td>{{ nameFor(levels, group.level?.id) }}</td>
          <td>{{ nameFor(sheikhs, group.sheikh?.id) }}</td>
          <td>{{ group.studentCount }}</td>
          <td>{{ joinNames(group.students) }}</td>
          <td>
            <div class="button-row">
              <button class="secondary" type="button" @click="edit(group)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="remove(group)">{{ $t("common.delete") }}</button>
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
const levels = ref([]);
const sheikhs = ref([]);
const students = ref([]);
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
    levelId: "",
    sheikhId: "",
    studentIds: []
  };
}

async function load() {
  try {
    error.value = "";
    const [groupsData, levelsData, sheikhsData, studentsData] = await Promise.all([
      apiGet("/groups", { page: page.value, size: pageSize }),
      apiGet("/levels", { page: 0, size: 100 }),
      apiGet("/sheikhs", { page: 0, size: 100 }),
      apiGet("/students", { page: 0, size: 100 })
    ]);
    items.value = groupsData.content ?? groupsData;
    totalPages.value = groupsData.totalPages ?? 1;
    levels.value = levelsData.content ?? levelsData;
    sheikhs.value = sheikhsData.content ?? sheikhsData;
    students.value = studentsData.content ?? studentsData;
  } catch (err) {
    error.value = err.message;
  }
}

function edit(group) {
  form.value = {
    id: group.id,
    name: group.name || "",
    code: group.code || "",
    levelId: group.level?.id || "",
    sheikhId: group.sheikh?.id || "",
    studentIds: (group.students || []).map((student) => student.id)
  };
}

function reset() {
  form.value = emptyForm();
}

function buildPayload() {
  return {
    name: form.value.name,
    code: form.value.code || null,
    levelId: form.value.levelId ? Number(form.value.levelId) : null,
    sheikhId: form.value.sheikhId ? Number(form.value.sheikhId) : null,
    studentIds: form.value.studentIds.map((id) => Number(id))
  };
}

function nameFor(list, id) {
  return list.find((item) => item.id === id)?.name || "";
}

function joinNames(selected) {
  if (!selected || selected.length === 0) {
    return "";
  }
  return selected.map((item) => item.name).join(", ");
}

async function submit() {
  try {
    error.value = "";
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/groups/${form.value.id}`, payload);
    } else {
      await apiPost("/groups", payload);
      page.value = 0;
    }
    reset();
    await load();
  } catch (err) {
    error.value = err.message;
  }
}

async function remove(group) {
  if (!group?.id) {
    return;
  }
  try {
    error.value = "";
    await apiDelete(`/groups/${group.id}`);
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
