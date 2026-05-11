<template>
  <SheikhsForm
    v-if="showForm"
    :form="form"
    :submitting="submitting"
    @submit="submit"
    @cancel="showListView"
    @list="showListView"
  />
  <SheikhsList
    v-else
    :items="items"
    :loading="loading"
    :page="page"
    :totalPages="totalPages"
    @new="newSheikh"
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
import SheikhsForm from "../components/SheikhsForm.vue";
import SheikhsList from "../components/SheikhsList.vue";

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
const pageSize = 10;

function emptyForm() {
  return { id: null, name: "", code: "", birthDate: "", phoneNumber: "", gender: "MALE" };
}

async function load() {
  loading.value = true;
  try {
    const data = await apiGet("/sheikhs", { page: page.value, size: pageSize });
    items.value = data.content ?? data;
    totalPages.value = data.totalPages ?? 1;
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  } finally {
    loading.value = false;
  }
}

function newSheikh() {
  form.value = emptyForm();
  showForm.value = true;
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
    gender: form.value.gender || null
  };
}

async function submit() {
  submitting.value = true;
  try {
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/sheikhs/${form.value.id}`, payload);
    } else {
      await apiPost("/sheikhs", payload);
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

function remove(sheikh) {
  if (!sheikh?.id) return;
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
        await apiDelete(`/sheikhs/${sheikh.id}`);
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
