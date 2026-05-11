<template>
  <PageLayout :title="studentName" icon="pi-chart-line">
    <template #actions>
      <Button
        :label="$t('reports.backToReports')"
        icon="pi pi-list"
        severity="secondary"
        @click="$router.push('/reports')"
      />
    </template>

    <p v-if="groupInfo" class="student-meta">{{ groupInfo }}</p>

    <div class="stat-cards">
      <article class="card stat-card">
        <span class="small-muted">{{ $t("reports.recitations") }}</span>
        <strong>{{ totalPages > 0 || recitations.length ? recitations.length + (totalPages > 1 ? '+' : '') : 0 }}</strong>
      </article>
      <article class="card stat-card">
        <span class="small-muted">{{ $t("reports.averageGrade") }}</span>
        <strong>{{ avgGrade }}</strong>
      </article>
      <article class="card stat-card">
        <span class="small-muted">{{ $t("reports.mistakes") }}</span>
        <strong>{{ totalMistakes }}</strong>
      </article>
    </div>

    <h3 class="section-title">{{ $t("reports.history") }}</h3>

    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("recitations.recitationDate") }}</th>
          <th>{{ $t("recitations.range") }}</th>
          <th>{{ $t("recitations.ayat") }}</th>
          <th>{{ $t("recitations.grade") }}</th>
          <th>{{ $t("recitations.totalMistakes") }}</th>
          <th>{{ $t("reports.topThreeMistakes") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in recitations" :key="r.id">
          <td class="ltr">{{ r.recitationDate || "—" }}</td>
          <td class="ltr">{{ rangeLabel(r) }}</td>
          <td>{{ r.numberOfAyat ?? "—" }}</td>
          <td>{{ r.grade ?? "—" }}</td>
          <td>{{ r.totalMistakes }}</td>
          <td>{{ topMistakesLabel(r.mistakes) }}</td>
        </tr>
        <tr v-if="!recitations.length">
          <td colspan="6" class="empty-row">{{ $t("reports.noHistory") }}</td>
        </tr>
      </tbody>
    </table>

    <div class="pager">
      <Button icon="pi pi-angle-right" severity="secondary" text rounded :disabled="page === 0" :title="$t('common.prev')" @click="changePage(-1)" />
      <span>{{ page + 1 }} / {{ totalPages || 1 }}</span>
      <Button icon="pi pi-angle-left" severity="secondary" text rounded :disabled="page + 1 >= totalPages" :title="$t('common.next')" @click="changePage(1)" />
    </div>
  </PageLayout>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import Button from "primevue/button";
import PageLayout from "../components/PageLayout.vue";
import { apiGet } from "../services/api";
import { SURAHS } from "../data/surahs.js";

const route = useRoute();
const router = useRouter();
const { t } = useI18n();
const toast = useToast();

const student = ref(null);
const recitations = ref([]);
const page = ref(0);
const totalPages = ref(1);
const pageSize = 20;

const studentName = computed(() => student.value?.name || t("reports.studentProgress"));
const groupInfo = computed(() => {
  const g = student.value?.recitationGroup;
  if (!g) return null;
  return g.level?.name ? `${g.name} · ${g.level.name}` : g.name;
});

const avgGrade = computed(() => {
  const graded = recitations.value.filter(r => r.grade != null);
  if (!graded.length) return "—";
  const avg = graded.reduce((sum, r) => sum + r.grade, 0) / graded.length;
  return avg.toFixed(1);
});

const totalMistakes = computed(() =>
  recitations.value.reduce((sum, r) => sum + (r.totalMistakes || 0), 0)
);

async function load() {
  try {
    const studentId = route.params.id;
    const [studentData, recitationsData] = await Promise.all([
      apiGet(`/students/${studentId}`),
      apiGet("/recitations", { studentId, page: page.value, size: pageSize })
    ]);
    student.value = studentData;
    recitations.value = recitationsData.content ?? recitationsData;
    totalPages.value = recitationsData.totalPages ?? 1;
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

function surahName(num) {
  return SURAHS.find(s => s.number === num)?.name || num;
}

function rangeLabel(r) {
  if (!r.fromSurah) return "—";
  return `${surahName(r.fromSurah)}:${r.fromAya} – ${surahName(r.toSurah)}:${r.toAya}`;
}

function topMistakesLabel(mistakes) {
  if (!mistakes?.length) return "—";
  return mistakes.slice(0, 3)
    .map(m => `${m.mistakeType?.name || ""} (${m.count})`)
    .filter(Boolean)
    .join("، ");
}

onMounted(load);
</script>

<style scoped>
.student-meta {
  color: var(--color-ink-muted);
  font-size: var(--text-sm);
  margin: calc(var(--space-2) * -1) 0 0;
}

.section-title {
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--color-ink);
  margin: 0;
}

.empty-row {
  text-align: center;
  color: var(--color-ink-muted);
  padding: var(--space-8);
  font-size: var(--text-sm);
}
</style>
