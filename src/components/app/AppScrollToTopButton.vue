<script setup lang='ts'>

import {GScrollTo, toggleAppScrollToTopButtonVisibility} from "@/composables/useAnimations.ts";

toggleAppScrollToTopButtonVisibility();
</script>

<template>
  <UButton
      id="app-scroll-to-top-button"
      class='fixed z-20 rounded-full right-4 bg-gray-200 dark:bg-background ui-ring bottom-20 scale-0 md:bottom-4  text-black dark:text-white hover:bg-primary hover:ring-primary hover:text-white'
      icon="mdi-light:chevron-up" square size="xl" @click="GScrollTo(0)">
  </UButton>
</template>