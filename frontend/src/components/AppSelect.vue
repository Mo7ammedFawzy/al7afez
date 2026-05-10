<template>
  <div class="app-field">
    <label v-if="label">{{ label }}<span v-if="required" class="req">*</span></label>
    <select v-bind="$attrs" :required="required" v-model="internalValue">
      <slot />
    </select>
    <span v-if="error" class="field-error">{{ error }}</span>
  </div>
</template>

<script setup>
import { computed } from "vue";

defineOptions({ inheritAttrs: false });

const props = defineProps({
  label:      { type: String,  default: undefined },
  modelValue: {                default: ""        },
  required:   { type: Boolean, default: false     },
  error:      { type: String,  default: undefined },
});

const emit = defineEmits(["update:modelValue"]);

const internalValue = computed({
  get: () => props.modelValue ?? "",
  set: (val) => emit("update:modelValue", val),
});
</script>
