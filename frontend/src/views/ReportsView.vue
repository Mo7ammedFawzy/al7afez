<template>
  <PageLayout :title="$t('reports.title')" icon="pi-chart-bar">
    <template #actions>
      <ProgressSpinner v-if="loading" style="width:28px;height:28px" strokeWidth="4" />
      <Button v-else :label="$t('reports.refresh')" icon="pi pi-refresh" severity="secondary" @click="load" />
    </template>
    <p class="small-muted">{{ $t("reports.subtitle") }}</p>
  </PageLayout>

  <div class="summary-grid">
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.students") }}</span>
      <strong v-if="!loading">{{ summary.totalStudents }}</strong>
      <Skeleton v-else height="2rem" width="3rem" />
    </article>
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.groups") }}</span>
      <strong v-if="!loading">{{ summary.totalGroups }}</strong>
      <Skeleton v-else height="2rem" width="3rem" />
    </article>
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.recitations") }}</span>
      <strong v-if="!loading">{{ summary.totalRecitations }}</strong>
      <Skeleton v-else height="2rem" width="3rem" />
    </article>
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.mistakes") }}</span>
      <strong v-if="!loading">{{ summary.totalMistakes }}</strong>
      <Skeleton v-else height="2rem" width="3rem" />
    </article>
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.averageGrade") }}</span>
      <strong v-if="!loading">{{ summary.averageGrade }}</strong>
      <Skeleton v-else height="2rem" width="3rem" />
    </article>
  </div>

  <PageLayout :title="$t('reports.topMistakes')" icon="pi-exclamation-triangle">
    <p class="small-muted">{{ $t("reports.topMistakesHelp") }}</p>
    <template v-if="loading">
      <div class="pill-list">
        <Skeleton v-for="n in 4" :key="n" height="3.5rem" class="pill-skel" />
      </div>
    </template>
    <template v-else>
      <p v-if="overview.topMistakes.length === 0" class="notice">{{ $t("reports.noData") }}</p>
      <div v-else class="pill-list">
        <div v-for="mistake in overview.topMistakes" :key="mistake.id" class="pill-card">
          <strong>{{ mistake.name }}</strong>
          <span>{{ mistake.count }} {{ $t("reports.occurrences") }}</span>
          <small>{{ mistake.share }}%</small>
        </div>
      </div>
    </template>
  </PageLayout>

  <PageLayout :title="$t('reports.byStudent')" icon="pi-users">
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("students.name") }}</th>
          <th>{{ $t("students.group") }}</th>
          <th>{{ $t("reports.recitations") }}</th>
          <th>{{ $t("reports.mistakes") }}</th>
          <th>{{ $t("reports.averageGrade") }}</th>
          <th>{{ $t("reports.lastRecitation") }}</th>
          <th>{{ $t("reports.topThreeMistakes") }}</th>
        </tr>
      </thead>
      <tbody>
        <template v-if="loading">
          <tr v-for="n in 4" :key="n">
            <td v-for="i in 7" :key="i"><Skeleton height="1rem" /></td>
          </tr>
        </template>
        <template v-else>
          <tr v-for="item in overview.students" :key="`student-${item.id}`">
            <td>
              <RouterLink :to="`/students/${item.id}/progress`" class="entity-link">
                {{ item.name }}
              </RouterLink>
            </td>
            <td>{{ item.secondaryLabel }}</td>
            <td>{{ item.recitationCount }}</td>
            <td>{{ item.mistakeCount }}</td>
            <td>{{ item.averageGrade }}</td>
            <td class="ltr">{{ item.latestRecitationDate || "—" }}</td>
            <td>{{ topMistakesLabel(item.topMistakes) }}</td>
          </tr>
          <tr v-if="!overview.students.length">
            <td colspan="7" class="empty-row">{{ $t("reports.noData") }}</td>
          </tr>
        </template>
      </tbody>
    </table>
  </PageLayout>

  <PageLayout :title="$t('reports.byGroup')" icon="pi-sitemap">
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("groups.name") }}</th>
          <th>{{ $t("groups.level") }}</th>
          <th>{{ $t("reports.recitations") }}</th>
          <th>{{ $t("reports.mistakes") }}</th>
          <th>{{ $t("reports.averageGrade") }}</th>
          <th>{{ $t("reports.lastRecitation") }}</th>
          <th>{{ $t("reports.topThreeMistakes") }}</th>
        </tr>
      </thead>
      <tbody>
        <template v-if="loading">
          <tr v-for="n in 3" :key="n">
            <td v-for="i in 7" :key="i"><Skeleton height="1rem" /></td>
          </tr>
        </template>
        <template v-else>
          <tr v-for="item in overview.groups" :key="`group-${item.id}`">
            <td>{{ item.name }}</td>
            <td>{{ item.secondaryLabel }}</td>
            <td>{{ item.recitationCount }}</td>
            <td>{{ item.mistakeCount }}</td>
            <td>{{ item.averageGrade }}</td>
            <td class="ltr">{{ item.latestRecitationDate || "—" }}</td>
            <td>{{ topMistakesLabel(item.topMistakes) }}</td>
          </tr>
          <tr v-if="!overview.groups.length">
            <td colspan="7" class="empty-row">{{ $t("reports.noData") }}</td>
          </tr>
        </template>
      </tbody>
    </table>
  </PageLayout>

  <PageLayout :title="$t('reports.byLevel')" icon="pi-list">
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("levels.name") }}</th>
          <th>{{ $t("reports.scope") }}</th>
          <th>{{ $t("reports.recitations") }}</th>
          <th>{{ $t("reports.mistakes") }}</th>
          <th>{{ $t("reports.averageGrade") }}</th>
          <th>{{ $t("reports.lastRecitation") }}</th>
          <th>{{ $t("reports.topThreeMistakes") }}</th>
        </tr>
      </thead>
      <tbody>
        <template v-if="loading">
          <tr v-for="n in 3" :key="n">
            <td v-for="i in 7" :key="i"><Skeleton height="1rem" /></td>
          </tr>
        </template>
        <template v-else>
          <tr v-for="item in overview.levels" :key="`level-${item.id}`">
            <td>{{ item.name }}</td>
            <td>{{ item.secondaryLabel }}</td>
            <td>{{ item.recitationCount }}</td>
            <td>{{ item.mistakeCount }}</td>
            <td>{{ item.averageGrade }}</td>
            <td class="ltr">{{ item.latestRecitationDate || "—" }}</td>
            <td>{{ topMistakesLabel(item.topMistakes) }}</td>
          </tr>
          <tr v-if="!overview.levels.length">
            <td colspan="7" class="empty-row">{{ $t("reports.noData") }}</td>
          </tr>
        </template>
      </tbody>
    </table>
  </PageLayout>

  <PageLayout :title="$t('reports.recentRecitations')" icon="pi-book">
    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("recitations.recitationDate") }}</th>
          <th>{{ $t("recitations.student") }}</th>
          <th>{{ $t("groups.name") }}</th>
          <th>{{ $t("levels.name") }}</th>
          <th>{{ $t("reports.mistakes") }}</th>
          <th>{{ $t("recitations.grade") }}</th>
        </tr>
      </thead>
      <tbody>
        <template v-if="loading">
          <tr v-for="n in 5" :key="n">
            <td v-for="i in 6" :key="i"><Skeleton height="1rem" /></td>
          </tr>
        </template>
        <template v-else>
          <tr v-for="item in overview.recentRecitations" :key="`recent-${item.id}`">
            <td class="ltr">{{ item.recitationDate }}</td>
            <td>
              <RouterLink v-if="item.student?.id" :to="`/students/${item.student.id}/progress`" class="entity-link">
                {{ item.student?.name || "—" }}
              </RouterLink>
              <span v-else>—</span>
            </td>
            <td>{{ item.group?.name || "—" }}</td>
            <td>{{ item.level?.name || "—" }}</td>
            <td>{{ item.totalMistakes }}</td>
            <td>{{ item.grade ?? "—" }}</td>
          </tr>
          <tr v-if="!overview.recentRecitations.length">
            <td colspan="6" class="empty-row">{{ $t("reports.noData") }}</td>
          </tr>
        </template>
      </tbody>
    </table>
  </PageLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "primevue/usetoast";
import { RouterLink } from "vue-router";
import Button from "primevue/button";
import ProgressSpinner from "primevue/progressspinner";
import Skeleton from "primevue/skeleton";
import PageLayout from "../components/PageLayout.vue";
import { apiGet } from "../services/api";

const { t } = useI18n();
const toast = useToast();

const loading = ref(false);

const overview = reactive({
  topMistakes: [],
  students: [],
  groups: [],
  levels: [],
  recentRecitations: []
});
const summary = reactive({
  totalStudents: 0,
  totalGroups: 0,
  totalLevels: 0,
  totalRecitations: 0,
  totalMistakes: 0,
  averageGrade: 0
});

async function load() {
  loading.value = true;
  try {
    const data = await apiGet("/reports/overview");
    Object.assign(summary, data.summary || {});
    overview.topMistakes = data.topMistakes || [];
    overview.students = data.students || [];
    overview.groups = data.groups || [];
    overview.levels = data.levels || [];
    overview.recentRecitations = data.recentRecitations || [];
  } catch (err) {
    toast.add({ severity: "error", summary: t("common.error"), detail: err.message, life: 5000 });
  } finally {
    loading.value = false;
  }
}

function topMistakesLabel(items) {
  if (!items?.length) return "—";
  return items.map(item => `${item.name} (${item.count})`).join("، ");
}

onMounted(load);
</script>

<style scoped>
.empty-row {
  text-align: center;
  color: var(--color-ink-muted);
  padding: var(--space-8);
  font-size: var(--text-sm);
}

.entity-link {
  color: var(--color-primary-600);
  text-decoration: none;
  font-weight: 500;
}

.entity-link:hover {
  text-decoration: underline;
}

.pill-skel {
  border-radius: var(--radius-md);
}
</style>
