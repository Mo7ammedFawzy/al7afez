import ObjectChecker from "@/utils/ObjectChecker.ts";

export default class CollectionUtils {

  static deleteEmptyValues(obj: Record<string, any>) {
    for (const key of Object.keys(obj)) {
      if (ObjectChecker.isEmptyOrNullish(obj[key]))
        delete obj[key];
    }
  }

  static findInArray<T>(el: T, array: T[]): T | undefined {
    return array.find(elInArray => elInArray === el);
  }

  static findInArrayWithKey<T extends Record<string, any>>(el: any, array: T[], key: string): T | undefined {
    return array.find(obj => {
      return JSON.stringify(obj[key]) === JSON.stringify(el)
    });
  }

  static removeFromArrayWithKey<T>(toDelete: T, array: any[], key: string): void {
    const indexOfDeleted = array.findIndex((item) => item[key] === toDelete);
    array.splice(indexOfDeleted, 1);
  }

  static isNotLastElementInArray(o: any, array: any[]): boolean {
    return !this.isLastElementInArray(o, array);
  }

  static isLastElementInArray(o: any, array: any[]): boolean {
    return array[array.length - 1] === o;
  }
}