<template>
  <LevelsForm
    v-if="showForm"
    :form="form"
    :submitting="submitting"
    @submit="submit"
    @cancel="showListView"
    @list="showListView"
  />
  <LevelsList
    v-else
    :items="items"
    :loading="loading"
    :page="page"
    :totalPages="totalPages"
    :totalItems="totalItems"
    @new="newLevel"
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
import LevelsForm from "../components/LevelsForm.vue";
import LevelsList from "../components/LevelsList.vue";

const { t } = useI18n();
const toast = useToast();
const confirmDialog = useConfirm();

const items = ref([]);
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const showForm = ref(false);
const loading = ref(false);
const submitting = ref(false);
const totalItems = ref(0);
const pageSize = 10;

function emptyForm() {
  return { id: null, name: "", code: "", fromSurah: null, toSurah: null, fromAya: null, toAya: null, numberOfAyatPerSession: null };
}

async function load() {
  loading.value = true;
  try {
    const data = await apiGet("/levels", { page: page.value, size: pageSize });
    items.value = data.content ?? data;
    totalPages.value = data.totalPages ?? 1;
    totalItems.value = data.totalElements ?? items.value.length;
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  } finally {
    loading.value = false;
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
  submitting.value = true;
  try {
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/levels/${form.value.id}`, payload);
    } else {
      await apiPost("/levels", payload);
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

function remove(level) {
  if (!level?.id) return;
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
        await apiDelete(`/levels/${level.id}`);
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
