import {isRef, unref, watch} from "vue";
import {LocationQuery, RouteLocationRaw, useRoute} from "vue-router";
import {MaybeRefOrGetter, useDateFormat} from "@vueuse/core";
import ObjectChecker from "@/utils/ObjectChecker.ts";
import router from "@/router";

export default class CommonUtils {
  static images = import.meta.glob('@/assets/imgs/**/*', {eager: true, import: 'default'});

  static getImgUrl(imgPath?: string): string | undefined {
    if (!imgPath)
      return;
    const base = "/src/assets/imgs";
    const image = this.images[base + imgPath];
    if (ObjectChecker.isString(image))
      return image;
  }

  static toSlug(title: string) {
    return title.replaceAll(" ", "-");
  }

  static slugToTitle(slug: string) {
    return slug.split("-").map(word => word[0].toUpperCase() + word.substring(1)).join(" ")
  }

  static onRouteChanged(cb: () => void) {
    const route = useRoute();
    watch(() => route.fullPath, () => {
      cb();
    })
  }

  static onQueryChanged(cb: (query: LocationQuery) => void) {
    const route = useRoute();
    watch(() => route.query, () => cb(route.query))
  }

  static unrefs(obj: Record<string, MaybeRefOrGetter<any>>) {
    const objCopy = obj;
    for (const key in objCopy) {
      if (isRef(objCopy[key]))
        objCopy[key] = unref(objCopy[key])
    }
    return objCopy;
  }

  static navigateTo(to: RouteLocationRaw) {
    router.push(to).then()
  }

  static async waitFor(ms: number = 250) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  static getDisabledUI() {
    return {ui: {base: 'rounded-lg disabled:!disabled-btn aria-disabled:!disabled-btn'}}
  }

  static toCapitalize(text: string): string {
    return text.split(" ").reduce((sentence, acc) => sentence + " " + acc.charAt(0).toUpperCase() + acc.substring(1), "")
  }

  static formattedDate(date: Date | string | undefined) {
    return useDateFormat(date, 'YYYY-MM-DD HH:mm').value
  }
}