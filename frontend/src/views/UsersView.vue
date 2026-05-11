<template>
  <UsersForm
    v-if="showForm"
    :form="form"
    :sheikhs="sheikhs"
    @submit="submit"
    @cancel="showListView"
    @list="showListView"
    @changePassword="changePassword"
  />
  <UsersList
    v-else
    :items="items"
    :page="page"
    :totalPages="totalPages"
    @new="newUser"
    @edit="edit"
    @remove="remove"
    @changePage="changePage"
  />
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { apiDelete, apiGet, apiPatch, apiPost, apiPut } from "../services/api";
import UsersForm from "../components/UsersForm.vue";
import UsersList from "../components/UsersList.vue";

const { t } = useI18n();
const toast = useToast();

const items = ref([]);
const sheikhs = ref([]);
const form = ref(emptyForm());
const page = ref(0);
const totalPages = ref(1);
const showForm = ref(false);
const pageSize = 10;

function emptyForm() {
  return { id: null, name: "", code: "", username: "", password: "", sheikhId: "" };
}

async function load() {
  try {
    const [usersData, sheikhsData] = await Promise.all([
      apiGet("/users", { page: page.value, size: pageSize }),
      apiGet("/sheikhs", { page: 0, size: 100 })
    ]);
    items.value = usersData.content ?? usersData;
    totalPages.value = usersData.totalPages ?? 1;
    sheikhs.value = sheikhsData.content ?? sheikhsData;
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  }
}

function newUser() {
  form.value = emptyForm();
  showForm.value = true;
}

function edit(user) {
  form.value = {
    id: user.id,
    name: user.name || "",
    code: user.code || "",
    username: user.username || "",
    password: "",
    sheikhId: user.sheikh?.id || ""
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
    username: form.value.username,
    password: form.value.password || null,
    sheikhId: form.value.sheikhId ? Number(form.value.sheikhId) : null
  };
}

async function submit() {
  try {
    const payload = buildPayload();
    if (form.value.id) {
      await apiPut(`/users/${form.value.id}`, payload);
    } else {
      await apiPost("/users", payload);
      page.value = 0;
    }
    showForm.value = false;
    await load();
    toast.add({ severity: "success", summary: t("common.saved"), life: 2000 });
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  }
}

async function changePassword(newPassword) {
  if (!form.value.id || !newPassword) return;
  try {
    await apiPatch(`/users/${form.value.id}/password`, { password: newPassword });
    toast.add({ severity: "success", summary: t("common.saved"), life: 2000 });
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  }
}

async function remove(user) {
  if (!user?.id) return;
  if (!confirm(t("common.deleteConfirm"))) return;
  try {
    await apiDelete(`/users/${user.id}`);
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
