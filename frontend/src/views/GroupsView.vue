<template>
  <GroupsForm
    v-if="showForm"
    :form="form"
    :levels="levels"
    :sheikhs="sheikhs"
    @submit="submit"
    @cancel="showListView"
    @list="showListView"
  />
  <GroupsList
    v-else
    :items="items"
    :totalPages="totalPages"
    :page="page"
    @new="newGroup"
    @edit="edit"
    @remove="remove"
    @changePage="changePage"
  />
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { apiDelete, apiGet, apiPost, apiPut } from "../services/api";
import GroupsList from "../components/GroupsList.vue";
import GroupsForm from "../components/GroupsForm.vue";

const { t } = useI18n();
const toast = useToast();

const items = ref([]);
const levels = ref([]);
const sheikhs = ref([]);
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const showForm = ref(false);
const pageSize = 10;

function emptyForm() {
  return { id: null, name: "", code: "", levelId: "", sheikhId: "" };
}

async function load() {
  try {
    const [groupsData, levelsData, sheikhsData] = await Promise.all([
      apiGet("/groups", { page: page.value, size: pageSize }),
      apiGet("/levels", { page: 0, size: 100 }),
      apiGet("/sheikhs", { page: 0, size: 100 })
    ]);
    items.value = groupsData.content ?? groupsData;
    totalPages.value = groupsData.totalPages ?? 1;
    levels.value = levelsData.content ?? levelsData;
    sheikhs.value = sheikhsData.content ?? sheikhsData;
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  }
}

function newGroup() {
  form.value = emptyForm();
  showForm.value = true;
}

function edit(group) {
  form.value = {
    id: group.id,
    name: group.name || "",
    code: group.code || "",
    levelId: group.level?.id || "",
    sheikhId: group.sheikh?.id || ""
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
    levelId: form.value.levelId ? Number(form.value.levelId) : null,
    sheikhId: form.value.sheikhId ? Number(form.value.sheikhId) : null
  };
}

async function submit() {
  try {
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/groups/${form.value.id}`, payload);
    } else {
      await apiPost("/groups", payload);
      page.value = 0;
    }
    showForm.value = false;
    await load();
    toast.add({ severity: "success", summary: t("common.saved"), life: 2000 });
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  }
}

async function remove(group) {
  if (!group?.id) return;
  if (!confirm(t("common.deleteConfirm"))) return;
  try {
    await apiDelete(`/groups/${group.id}`);
    await load();
    if (items.value.length === 0 && page.value > 0) {
      page.value -= 1;
      await load();
    }
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
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
