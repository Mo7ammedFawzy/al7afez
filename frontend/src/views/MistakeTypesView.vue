<template>
  <MistakeTypesForm
    v-if="showForm"
    :form="form"
    :parentOptions="parentOptions"
    @submit="submit"
    @cancel="showListView"
    @list="showListView"
  />
  <MistakeTypesList
    v-else
    :items="items"
    :loading="loading"
    :page="page"
    :totalPages="totalPages"
    @new="newType"
    @edit="edit"
    @remove="remove"
    @changePage="changePage"
  />
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { useConfirm } from "primevue/useconfirm";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";
import MistakeTypesForm from "../components/MistakeTypesForm.vue";
import MistakeTypesList from "../components/MistakeTypesList.vue";

const { t } = useI18n();
const toast = useToast();
const confirmDialog = useConfirm();

const items = ref([]);
const allTypes = ref([]);
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const showForm = ref(false);
const loading = ref(false);
const pageSize = 10;

const parentOptions = computed(() => allTypes.value.filter(item => item.id !== form.value.id));

function emptyForm() {
  return { id: null, name: "", code: "", parentId: "" };
}

async function load() {
  loading.value = true;
  try {
    const [pagedData, allData] = await Promise.all([
      apiGet("/mistake-types", { page: page.value, size: pageSize }),
      apiGet("/mistake-types", { page: 0, size: 200 })
    ]);
    items.value = pagedData.content ?? pagedData;
    totalPages.value = pagedData.totalPages ?? 1;
    allTypes.value = allData.content ?? allData;
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  } finally {
    loading.value = false;
  }
}

function newType() {
  form.value = emptyForm();
  showForm.value = true;
}

function edit(type) {
  form.value = {
    id: type.id,
    name: type.name || "",
    code: type.code || "",
    parentId: type.parent?.id || ""
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
    parentId: form.value.parentId ? Number(form.value.parentId) : null
  };
}

async function submit() {
  try {
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/mistake-types/${form.value.id}`, payload);
    } else {
      await apiPost("/mistake-types", payload);
      page.value = 0;
    }
    showForm.value = false;
    await load();
    toast.add({ severity: "success", summary: t("common.saved"), life: 2000 });
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  }
}

function remove(type) {
  if (!type?.id) return;
  confirmDialog.require({
    message: t("mistakeTypes.deleteConfirm", { name: type.name }),
    header: t("common.deleteTitle"),
    icon: "pi pi-exclamation-triangle",
    acceptProps: { severity: "danger" },
    rejectProps: { severity: "secondary", outlined: true },
    acceptLabel: t("common.delete"),
    rejectLabel: t("common.cancel"),
    accept: async () => {
      try {
        await apiDelete(`/mistake-types/${type.id}`);
        if (form.value.id === type.id) showListView();
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
