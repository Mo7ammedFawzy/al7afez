<template>
  <div v-if="error" class="popup-overlay" @click.self="error.value = ''">
    <div class="popup-card">
      <p>{{ error }}</p>
      <button class="secondary" type="button" @click="error.value = ''">{{ $t("common.cancel") }}</button>
    </div>
  </div>

  <StudentsForm
    v-if="showForm"
    :form="form"
    :groups="groups"
    @submit="submit"
    @cancel="cancelEdit"
    @list="showListView"
  />

  <StudentsList
    v-else
    :items="items"
    :page="page"
    :totalPages="totalPages"
    @new="newStudent"
    @edit="edit"
    @remove="remove"
    @changePage="changePage"
  />
</template>

<script setup>
import { onMounted, ref } from "vue";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";
import StudentsForm from "../components/StudentsForm.vue";
import StudentsList from "../components/StudentsList.vue";

const items = ref([]);
const groups = ref([]);
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

function newStudent() {
  form.value = emptyForm();
  showForm.value = true;
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
    form.value = emptyForm();
    showForm.value = true;
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
