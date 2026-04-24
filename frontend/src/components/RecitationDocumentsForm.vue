<template>
  <section class="card">
    <h2>{{ form.id ? $t("recitations.edit") : $t("recitations.new") }}</h2>
    <form class="grid grid-2" @submit.prevent="handleSubmit">
      <div>
        <label>{{ $t("recitations.code") }}</label>
        <input v-model="form.code" required />
      </div>
      <div>
        <label>{{ $t("recitations.recitationDate") }}</label>
        <input v-model="form.recitationDate" type="date" />
      </div>
      <div>
        <label>{{ $t("recitations.student") }}</label>
        <select v-model="form.studentId">
          <option value="">{{ $t("common.select") }} {{ $t("recitations.student") }}</option>
          <option v-for="student in students" :key="student.id" :value="student.id">
            {{ student.name }}
          </option>
        </select>
      </div>
      <div>
        <label>{{ $t("recitations.numberOfAyat") }}</label>
        <input v-model.number="form.numberOfAyat" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.fromSurah") }}</label>
        <input v-model.number="form.fromSurah" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.toSurah") }}</label>
        <input v-model.number="form.toSurah" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.fromAya") }}</label>
        <input v-model.number="form.fromAya" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.toAya") }}</label>
        <input v-model.number="form.toAya" type="number" min="1" />
      </div>
      <div>
        <label>{{ $t("recitations.grade") }}</label>
        <input v-model.number="form.grade" type="number" min="0" max="100" />
      </div>
      <div class="field-span-2">
        <label>{{ $t("recitations.notes") }}</label>
        <textarea v-model="form.notes" rows="3" />
      </div>
      <div class="field-span-2">
        <div class="section-header">
          <div>
            <strong>{{ $t("recitations.mistakeLog") }}</strong>
            <p class="small-muted">{{ $t("recitations.mistakeLogHelp") }}</p>
          </div>
          <button class="secondary" type="button" @click="handleAddMistake">{{ $t("recitations.addMistake") }}</button>
        </div>
        <div v-if="form.mistakes.length === 0" class="notice">
          {{ $t("recitations.noMistakesLogged") }}
        </div>
        <div v-for="(mistake, index) in form.mistakes" :key="index" class="mistake-row">
          <select v-model="mistake.mistakeTypeId">
            <option value="">{{ $t("common.select") }} {{ $t("mistakeTypes.mistakeType") }}</option>
            <option v-for="type in mistakeTypes" :key="type.id" :value="type.id">
              {{ type.name }}
            </option>
          </select>
          <input v-model.number="mistake.count" type="number" min="1" :placeholder="$t('recitations.count')" />
          <input v-model="mistake.note" :placeholder="$t('recitations.mistakeNote')" />
          <button class="danger" type="button" @click="handleRemoveMistake(index)">{{ $t("common.delete") }}</button>
        </div>
      </div>
      <div class="button-row">
        <button class="primary" type="submit">{{ form.id ? $t("common.save") : $t("common.create") }}</button>
        <button class="secondary" type="button" @click="handleCancel">{{ $t("common.cancel") }}</button>
        <button class="secondary" type="button" @click="handleList">{{ $t("common.list") }}</button>
      </div>
    </form>
  </section>
</template>

<script setup>
defineProps({
  form: {
    type: Object,
    required: true
  },
  students: {
    type: Array,
    default: () => []
  },
  mistakeTypes: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(["submit", "cancel", "list"]);

function handleSubmit() {
  emit("submit");
}

function handleCancel() {
  emit("cancel");
}

function handleList() {
  emit("list");
}

function handleAddMistake() {
  form.mistakes.push({
    mistakeTypeId: "",
    count: 1,
    note: ""
  });
}

function handleRemoveMistake(index) {
  form.mistakes.splice(index, 1);
}
</script>
