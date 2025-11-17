export default class ObjectChecker {

  static isNotEmptyOrNull<T>(o: T | null | undefined): o is T {
    return !this.isEmptyOrNull(o);
  }

  static isEmptyOrNull<T>(o: T | null | undefined):o is (null | undefined) {
    return this.isEmpty(o) || this.isNull(o);
  }

  static isEmpty(o: unknown): boolean {
    if (Array.isArray(o) || typeof o === "string")
      return o.length === 0
    else if (o && typeof o == "object")
      return Object.keys(o).length === 0
    else
      return false;
  }

  static isNotNull<T>(o: T | null | undefined): o is NonNullable<T> {
    return o !== null && o !== undefined;
  }

  static isNull(o: unknown): o is null | undefined {
    return !this.isNotNull(o);
  }

  static isAnyNotEmptyOrNull(...list: unknown[]): boolean {
    return !this.isAnyEmptyOrNull(list);
  }

  static isAnyEmptyOrNull<T extends unknown>(...list: (T | null | undefined)[]) {
    return list.some(el => this.isEmptyOrNull(el));
  }


  public static isString(o: unknown): o is string {
    return typeof o === "string";
  }

  static isNumber(o: unknown): o is number {
    return typeof o === "number" && !isNaN(o);
  }

  static isObject(o: unknown): o is object {
    return o !== null && typeof o === "object" && !this.isArray(o);
  }

  static isArray(o: unknown): o is any[] {
    return Array.isArray(o);
  }

  static isEmptyOrNullish<T>(o: T | null | undefined): o is null | undefined {
    if (o == null)
      return true;
    else if (this.isString(o))
      return this.isEmptyString(o);
    else if (this.isArray(o))
      return this.isEmptyArray(o);
    else if (this.isObject(o))
      return this.isEmptyObject(o);
    return false;
  }

  static isNotEmptyOrNullish<T>(o: T | null | undefined): o is NonNullable<T> {
    return !this.isEmptyOrNullish(o);
  }

  private static isEmptyString(o: string) {
    return o.trim().length < 1;
  }

  static isEmptyObject(o: object) {
    return Object.keys(o).length === 0;
  }

  private static isEmptyArray(o: any[]) {
    return o.length === 0;
  }

  static isLastElement(element: any, array: any[]) {
    return array[array.length - 1] === element;
  }
}