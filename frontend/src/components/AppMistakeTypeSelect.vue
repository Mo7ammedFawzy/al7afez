<template>
  <div class="app-field">
    <label v-if="label" :for="uid">
      {{ label }}<span v-if="required" class="req" aria-hidden="true">*</span>
    </label>
    <select
      :id="uid"
      :required="required"
      :aria-invalid="error ? true : undefined"
      :aria-describedby="error ? `${uid}-err` : undefined"
      v-model="internalValue"
    >
      <option value="">{{ placeholder || $t('common.select') }}</option>
      <option v-for="node in treeFlat" :key="node.id" :value="node.id">
        {{ '   '.repeat(node.depth) }}{{ node.depth > 0 ? '— ' : '' }}{{ node.name }}
      </option>
    </select>
    <span v-if="error" :id="`${uid}-err`" class="field-error" role="alert">{{ error }}</span>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance } from 'vue';

const uid = `app-field-${getCurrentInstance().uid}`;

const props = defineProps({
  label:       { type: String,  default: undefined },
  modelValue:  {                default: ''        },
  options:     { type: Array,   default: () => []  },
  required:    { type: Boolean, default: false      },
  error:       { type: String,  default: undefined  },
  placeholder: { type: String,  default: undefined  },
});

const emit = defineEmits(['update:modelValue']);

const internalValue = computed({
  get: () => props.modelValue ?? '',
  set: val => emit('update:modelValue', val),
});

const treeFlat = computed(() => {
  const byId = {};
  for (const o of props.options) {
    byId[o.id] = { ...o, children: [] };
  }

  const roots = [];
  for (const node of Object.values(byId)) {
    const parentId = node.parent?.id;
    if (parentId && byId[parentId]) {
      byId[parentId].children.push(node);
    } else {
      roots.push(node);
    }
  }

  const flat = [];
  function walk(nodes, depth) {
    for (const node of nodes) {
      flat.push({ id: node.id, name: node.name, depth });
      if (node.children.length) walk(node.children, depth + 1);
    }
  }
  walk(roots, 0);
  return flat;
});
</script>
