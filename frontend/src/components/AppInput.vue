<template>
  <div class="app-field">
    <label v-if="label" :for="uid">
      {{ label }}<span v-if="required" class="req" aria-hidden="true">*</span>
    </label>
    <input
      v-bind="$attrs"
      :id="uid"
      :type="type"
      :required="required"
      :aria-invalid="error ? true : undefined"
      :aria-describedby="error ? `${uid}-err` : undefined"
      v-model="internalValue"
    />
    <span v-if="error" :id="`${uid}-err`" class="field-error" role="alert">{{ error }}</span>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance } from "vue";

defineOptions({ inheritAttrs: false });

const uid = `app-field-${getCurrentInstance().uid}`;

const props = defineProps({
  label:      { type: String,           default: undefined },
  modelValue: { type: [String, Number], default: ""        },
  type:       { type: String,           default: "text"    },
  required:   { type: Boolean,          default: false     },
  error:      { type: String,           default: undefined },
});

const emit = defineEmits(["update:modelValue"]);

const internalValue = computed({
  get: () => props.modelValue ?? "",
  set: (val) => emit("update:modelValue", val),
});
</script>
