<template>
  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("reports.title") }}</h2>
        <p class="small-muted">{{ $t("reports.subtitle") }}</p>
      </div>
      <button class="secondary" type="button" @click="load">{{ $t("reports.refresh") }}</button>
    </div>
    <div v-if="error" class="notice">{{ error }}</div>
  </section>

  <section class="summary-grid">
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.students") }}</span>
      <strong>{{ summary.totalStudents }}</strong>
    </article>
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.groups") }}</span>
      <strong>{{ summary.totalGroups }}</strong>
    </article>
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.recitations") }}</span>
      <strong>{{ summary.totalRecitations }}</strong>
    </article>
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.mistakes") }}</span>
      <strong>{{ summary.totalMistakes }}</strong>
    </article>
    <article class="card stat-card">
      <span class="small-muted">{{ $t("reports.cards.averageGrade") }}</span>
      <strong>{{ summary.averageGrade }}</strong>
    </article>
  </section>

  <section class="card">
    <div class="section-header">
      <div>
        <h2>{{ $t("reports.topMistakes") }}</h2>
        <p class="small-muted">{{ $t("reports.topMistakesHelp") }}</p>
      </div>
    </div>
    <div v-if="overview.topMistakes.length === 0" class="notice">{{ $t("reports.noData") }}</div>
    <div v-else class="pill-list">
      <div v-for="mistake in overview.topMistakes" :key="mistake.id" class="pill-card">
        <strong>{{ mistake.name }}</strong>
        <span>{{ mistake.count }} {{ $t("reports.occurrences") }}</span>
        <small>{{ mistake.share }}%</small>
      </div>
    </div>
  </section>

  <section class="card">
    <h2>{{ $t("reports.byStudent") }}</h2>
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
        <tr v-for="item in overview.students" :key="`student-${item.id}`">
          <td>{{ item.name }}</td>
          <td>{{ item.secondaryLabel }}</td>
          <td>{{ item.recitationCount }}</td>
          <td>{{ item.mistakeCount }}</td>
          <td>{{ item.averageGrade }}</td>
          <td>{{ item.latestRecitationDate || "-" }}</td>
          <td>{{ topMistakesLabel(item.topMistakes) }}</td>
        </tr>
      </tbody>
    </table>
  </section>

  <section class="card">
    <h2>{{ $t("reports.byGroup") }}</h2>
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
        <tr v-for="item in overview.groups" :key="`group-${item.id}`">
          <td>{{ item.name }}</td>
          <td>{{ item.secondaryLabel }}</td>
          <td>{{ item.recitationCount }}</td>
          <td>{{ item.mistakeCount }}</td>
          <td>{{ item.averageGrade }}</td>
          <td>{{ item.latestRecitationDate || "-" }}</td>
          <td>{{ topMistakesLabel(item.topMistakes) }}</td>
        </tr>
      </tbody>
    </table>
  </section>

  <section class="card">
    <h2>{{ $t("reports.byLevel") }}</h2>
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
        <tr v-for="item in overview.levels" :key="`level-${item.id}`">
          <td>{{ item.name }}</td>
          <td>{{ item.secondaryLabel }}</td>
          <td>{{ item.recitationCount }}</td>
          <td>{{ item.mistakeCount }}</td>
          <td>{{ item.averageGrade }}</td>
          <td>{{ item.latestRecitationDate || "-" }}</td>
          <td>{{ topMistakesLabel(item.topMistakes) }}</td>
        </tr>
      </tbody>
    </table>
  </section>

  <section class="card">
    <h2>{{ $t("reports.recentRecitations") }}</h2>
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
        <tr v-for="item in overview.recentRecitations" :key="`recent-${item.id}`">
          <td>{{ item.recitationDate }}</td>
          <td>{{ item.student?.name || "-" }}</td>
          <td>{{ item.group?.name || "-" }}</td>
          <td>{{ item.level?.name || "-" }}</td>
          <td>{{ item.totalMistakes }}</td>
          <td>{{ item.grade ?? "-" }}</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { apiGet } from "../services/api";

const error = ref("");
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
  try {
    error.value = "";
    const data = await apiGet("/reports/overview");
    Object.assign(summary, data.summary || {});
    overview.topMistakes = data.topMistakes || [];
    overview.students = data.students || [];
    overview.groups = data.groups || [];
    overview.levels = data.levels || [];
    overview.recentRecitations = data.recentRecitations || [];
  } catch (err) {
    error.value = err.message;
  }
}

function topMistakesLabel(items) {
  if (!items || items.length === 0) {
    return "-";
  }
  return items.map((item) => `${item.name} (${item.count})`).join(", ");
}

onMounted(load);
</script>
