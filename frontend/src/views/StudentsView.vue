<template>
  <section class="card">
    <h2>{{ $t("students.title") }}</h2>
    <p class="badge">{{ $t("students.subtitle") }}</p>
    <div v-if="error" class="notice">{{ error }}</div>
  </section>

  <section class="card">
    <h2>{{ form.id ? $t("students.edit") : $t("students.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="submit">
      <div>
        <label>{{ $t("students.name") }}</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>{{ $t("students.code") }}</label>
        <input v-model="form.code" :placeholder="$t('students.code')" />
      </div>
      <div>
        <label>{{ $t("students.birthDate") }}</label>
        <input v-model="form.birthDate" type="date" />
      </div>
      <div>
        <label>{{ $t("students.gender") }}</label>
        <select v-model="form.gender">
          <option value="MALE">{{ $t("common.male") }}</option>
          <option value="FEMALE">{{ $t("common.female") }}</option>
        </select>
      </div>
      <div>
        <label>{{ $t("students.phone") }}</label>
        <input v-model="form.phoneNumber" />
      </div>
      <div>
        <label>{{ $t("students.parentPhone") }}</label>
        <input v-model="form.parentPhoneNumber" />
      </div>
      <div>
        <label>{{ $t("students.group") }}</label>
        <select v-model="form.recitationGroupId">
          <option value="">{{ $t("students.noGroup") }}</option>
          <option v-for="group in groups" :key="group.id" :value="group.id">
            {{ group.name }}
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
    <h2>{{ $t("students.list") }}</h2>
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("students.name") }}</th>
          <th>{{ $t("students.group") }}</th>
          <th>{{ $t("students.level") }}</th>
          <th>{{ $t("students.gender") }}</th>
          <th>{{ $t("students.phone") }}</th>
          <th>{{ $t("students.parentPhone") }}</th>
          <th>{{ $t("students.birthDate") }}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="student in items" :key="student.id">
          <td>{{ student.name }}</td>
          <td>{{ student.recitationGroup?.name || $t("students.noGroup") }}</td>
          <td>{{ student.level?.name || "-" }}</td>
          <td>{{ student.gender === "MALE" ? $t("common.male") : $t("common.female") }}</td>
          <td>{{ student.phoneNumber }}</td>
          <td>{{ student.parentPhoneNumber }}</td>
          <td>{{ student.birthDate }}</td>
          <td>
            <div class="button-row">
              <button class="secondary" type="button" @click="edit(student)">{{ $t("common.edit") }}</button>
              <button class="danger" type="button" @click="remove(student)">{{ $t("common.delete") }}</button>
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
const groups = ref([]);
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
    parentPhoneNumber: "",
    gender: "MALE",
    recitationGroupId: ""
  };
}

async function load() {
  try {
    error.value = "";
    const [studentsData, groupsData] = await Promise.all([
      apiGet("/students", { page: page.value, size: pageSize }),
      apiGet("/groups", { page: 0, size: 100 })
    ]);
    items.value = studentsData.content ?? studentsData;
    totalPages.value = studentsData.totalPages ?? 1;
    groups.value = groupsData.content ?? groupsData;
  } catch (err) {
    error.value = err.message;
  }
}

function edit(student) {
  form.value = {
    id: student.id,
    name: student.name || "",
    code: student.code || "",
    birthDate: student.birthDate || "",
    phoneNumber: student.phoneNumber || "",
    parentPhoneNumber: student.parentPhoneNumber || "",
    gender: student.gender || "MALE",
    recitationGroupId: student.recitationGroup?.id || ""
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
    parentPhoneNumber: form.value.parentPhoneNumber || null,
    gender: form.value.gender || null,
    recitationGroupId: form.value.recitationGroupId ? Number(form.value.recitationGroupId) : null
  };
}

async function submit() {
  try {
    error.value = "";
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/students/${form.value.id}`, payload);
    } else {
      await apiPost("/students", payload);
      page.value = 0;
    }
    reset();
    await load();
  } catch (err) {
    error.value = err.message;
  }
}

async function remove(student) {
  if (!student?.id) {
    return;
  }
  try {
    error.value = "";
    await apiDelete(`/students/${student.id}`);
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
