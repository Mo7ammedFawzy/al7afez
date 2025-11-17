import {breakpointsTailwind, useBreakpoints} from "@vueuse/core";
import {computed, ref, watch} from "vue";

export const useUIBreakpoints = () => {

  const breakpoints = useBreakpoints({
    ...breakpointsTailwind,
    md: 900
  });
  const isMDScreen = ref(breakpoints.isGreaterOrEqual("md"));
  watch(breakpoints.current(), (currentValue) => {
    isMDScreen.value = currentValue.includes("md");
  })

  const isMobileScreen = computed(() => breakpoints.smallerOrEqual("md").value)

  return {isMDScreen, isMobileScreen, breakpoints, isNotMobileScreen: computed(() => !isMobileScreen.value)}
}