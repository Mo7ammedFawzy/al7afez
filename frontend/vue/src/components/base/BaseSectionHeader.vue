<script setup lang="ts">
withDefaults(defineProps<{
  title: string,
  text?: string
}>(), {
  text: "Lorem Ipsum Is Simply Dummy Text Of The Printing And Typesetting Industry."
})

</script>
<template>
  <div class='text-center px-3'>
    <div class="text-4xl font-bold capitalize text-gray-800 dark:text-white sm:text-5xl" v-text="title"/>
    <p v-text="text" class="mt-(--ui-gap) mb-6 text-xs text-gray-400 sm:text-sm md:text-base"/>
  </div>
</template>
