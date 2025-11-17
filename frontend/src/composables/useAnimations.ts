import {gsap} from "gsap"
import {CustomEase} from "gsap/CustomEase"
import {SplitText} from "gsap/SplitText";
import {ScrollToPlugin} from "gsap/ScrollToPlugin"
import {useDebounceFn, useEventListener} from "@vueuse/core";
import {onMounted} from "vue";

gsap.registerPlugin(CustomEase)
gsap.registerPlugin(ScrollToPlugin)
gsap.registerPlugin(SplitText)
CustomEase.create("hop", "0.9, 0, 0.1, 1");
CustomEase.create("hopSmooth", "0.8, 0.1, 0.2, 0.9");

export const GScrollTo = (target: ScrollToPlugin.Vars["y"], offsetY: number = 0) => {
  gsap.to(window, {
    scrollTo: {
      y: target,
      offsetY
    },
    ease: "Power4.easeInOut",
    duration: 1
  })
}
export const toggleAppScrollToTopButtonVisibility = () => {
  const appScrollToTopButton = "#app-scroll-to-top-button";
  onMounted(() => {
    useEventListener(window, "scroll", useDebounceFn(() => {
      let canShowButton = false;
      if (window.scrollY > 400)
        canShowButton = true;
      gsap.to(appScrollToTopButton, {
        scale: canShowButton ? 1 : 0,
        duration: .5,
        ease: "Power4.easeInOut"
      })
    }, 350))
  })
}