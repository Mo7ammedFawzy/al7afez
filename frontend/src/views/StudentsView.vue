<template>
  <StudentsForm
    v-if="showForm"
    :form="form"
    :groups="groups"
    :submitting="submitting"
    @submit="submit"
    @cancel="showListView"
    @list="showListView"
  />
  <StudentsList
    v-else
    :items="items"
    :loading="loading"
    :page="page"
    :totalPages="totalPages"
    :totalItems="totalItems"
    @new="newStudent"
    @edit="edit"
    @remove="remove"
    @changePage="changePage"
  />
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { useConfirm } from "primevue/useconfirm";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";
import StudentsForm from "../components/StudentsForm.vue";
import StudentsList from "../components/StudentsList.vue";

const { t } = useI18n();
const toast = useToast();
const confirmDialog = useConfirm();

const items = ref([]);
const groups = ref([]);
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const showForm = ref(false);
const loading = ref(false);
const submitting = ref(false);
const totalItems = ref(0);
const pageSize = 10;

function emptyForm() {
  return {
    id: null,
    name: "",
    code: "",
    birthDate: new Date().toISOString().split('T')[0],
    phoneNumber: "",
    parentPhoneNumber: "",
    gender: "MALE",
    recitationGroupId: ""
  };
}

async function load() {
  loading.value = true;
  try {
    const [studentsData, groupsData] = await Promise.all([
      apiGet("/students", { page: page.value, size: pageSize }),
      apiGet("/groups", { page: 0, size: 100 })
    ]);
    items.value = studentsData.content ?? studentsData;
    totalPages.value = studentsData.totalPages ?? 1;
    totalItems.value = studentsData.totalElements ?? items.value.length;
    groups.value = groupsData.content ?? groupsData;
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  } finally {
    loading.value = false;
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
  submitting.value = true;
  try {
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/students/${form.value.id}`, payload);
    } else {
      await apiPost("/students", payload);
      page.value = 0;
    }
    showForm.value = false;
    await load();
    toast.add({ severity: "success", summary: t("common.saved"), life: 2000 });
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  } finally {
    submitting.value = false;
  }
}

function remove(student) {
  if (!student?.id) return;
  confirmDialog.require({
    message: t("common.deleteConfirm"),
    header: t("common.deleteTitle"),
    icon: "pi pi-exclamation-triangle",
    acceptProps: { severity: "danger" },
    rejectProps: { severity: "secondary", outlined: true },
    acceptLabel: t("common.delete"),
    rejectLabel: t("common.cancel"),
    accept: async () => {
      try {
        await apiDelete(`/students/${student.id}`);
        toast.add({ severity: "success", summary: t("common.deleted"), life: 2000 });
        await load();
        if (items.value.length === 0 && page.value > 0) {
          page.value -= 1;
          await load();
        }
      } catch (err) {
        toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
      }
    }
  });
}

function changePage(delta) {
  const next = page.value + delta;
  if (next < 0 || next >= totalPages.value) return;
  page.value = next;
  load();
}

onMounted(load);
</script>
