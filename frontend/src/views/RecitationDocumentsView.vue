<template>
  <RecitationDocumentsForm
    v-if="showForm"
    :form="form"
    :students="students"
    :mistakeTypes="mistakeTypes"
    :submitting="submitting"
    @submit="submit"
    @cancel="showListView"
    @list="showListView"
  />
  <RecitationDocumentsList
    v-else
    :items="items"
    :loading="loading"
    :page="page"
    :totalPages="totalPages"
    :totalItems="totalItems"
    @new="newRecitation"
    @edit="edit"
    @remove="remove"
    @changePage="changePage"
  />
</template>

<script setup>
import { onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { useConfirm } from "primevue/useconfirm";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";
import RecitationDocumentsForm from "../components/RecitationDocumentsForm.vue";
import RecitationDocumentsList from "../components/RecitationDocumentsList.vue";

const { t } = useI18n();
const toast = useToast();
const confirmDialog = useConfirm();

const items = ref([]);
const students = ref([]);
const mistakeTypes = ref([]);
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
    id: null, code: "", recitationDate: new Date().toISOString().split('T')[0], studentId: "",
    fromSurah: null, toSurah: null, fromAya: null, toAya: null,
    numberOfAyat: null, grade: null, notes: "", mistakes: []
  };
}

async function load() {
  loading.value = true;
  try {
    const [recitationsData, studentsData, mistakeTypesData] = await Promise.all([
      apiGet("/recitations", { page: page.value, size: pageSize }),
      apiGet("/students", { page: 0, size: 100 }),
      apiGet("/mistake-types", { page: 0, size: 100 })
    ]);
    items.value = recitationsData.content ?? recitationsData;
    totalPages.value = recitationsData.totalPages ?? 1;
    totalItems.value = recitationsData.totalElements ?? items.value.length;
    students.value = studentsData.content ?? studentsData;
    mistakeTypes.value = mistakeTypesData.content ?? mistakeTypesData;
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  } finally {
    loading.value = false;
  }
}

watch(() => form.value.studentId, async (studentId) => {
  if (!showForm.value || form.value.id || !studentId) return;
  try {
    const suggestion = await apiGet("/recitations/suggest", { studentId });
    form.value.fromSurah = suggestion.fromSurah;
    form.value.fromAya = suggestion.fromAya;
    form.value.toSurah = suggestion.toSurah;
    form.value.toAya = suggestion.toAya;
    form.value.numberOfAyat = suggestion.numberOfAyat;
  } catch {
    // suggestion is best-effort; ignore errors
  }
});

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
    mistakes: (recitation.mistakes || []).map(m => ({
      mistakeTypeId: m.mistakeType?.id || ""
    }))
  };
  showForm.value = true;
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
      .filter(m => m.mistakeTypeId && m.count)
      .map(m => ({ mistakeTypeId: Number(m.mistakeTypeId), count: Number(m.count) }))
  };
}

async function submit() {
  submitting.value = true;
  try {
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/recitations/${form.value.id}`, payload);
    } else {
      await apiPost("/recitations", payload);
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

function remove(recitation) {
  if (!recitation?.id) return;
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
        await apiDelete(`/recitations/${recitation.id}`);
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
