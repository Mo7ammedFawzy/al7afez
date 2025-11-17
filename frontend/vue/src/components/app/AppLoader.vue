<template>
  <main id='app-loader'
        class='w-full h-full bg-background overflow-hidden fixed top-0 left-0 z-50 cursor-wait select-none translate-y-0'>
    <BaseWrapper class="h-full flex items-center justify-center">
      <div data-app-loader-wrapper class="absolute flex items-center justify-center h-full w-full left-0 bottom-0">
        <div class="fit flex items-end gap-2 opacity-0 -translate-y-2" data-app-loader-inner>
          <h1 class="capitalize text-5xl md:text-7xl dark:text-white" v-text="loaderTitle"/>
          <!--          <UIcon :name="loaderIcon" class="text-4xl md:text-6xl"/>-->
        </div>
      </div>
    </BaseWrapper>
  </main>
</template>