import {computed, ComputedRef, ref, Ref} from "vue";
import CommonUtils from "@/utils/CommonUtils.ts";

class ButtonActionKlass {
  private static _instance: ButtonActionKlass;
  private _isButtonClicked: Ref<boolean> = ref(false);

  static getInstance(): ButtonActionKlass {
    if (!this.instance)
      this.instance = new ButtonActionKlass();
    return this.instance;
  }

  isButtonDisabled(): ComputedRef<boolean> {
    return computed(() => this._isButtonClicked.value)
  }

  getDisabledButtonProp(...moreConditions: boolean[]): ComputedRef<any> {
    return computed(() => ({
      disabled: this.isButtonDisabled().value || moreConditions.some(c => Boolean(c))
    }))
  }

  getDisabledButtonProps(...moreConditions: boolean[]): ComputedRef<any> {
    return computed(() => ({...this.getDisabledButtonProp(...moreConditions).value, ...this.getDisabledButtonUI()}))
  }

  getDisabledButtonUI() {
    return {ui: {base: 'ui-disabled-btn'}}
  }

  async performAction(action: () => void) {
    this._isButtonClicked.value = true;
    await CommonUtils.waitFor(750);
    action();
    this._isButtonClicked.value = false;
  }


  static get instance(): ButtonActionKlass {
    return this._instance;
  }

  static set instance(value: ButtonActionKlass) {
    this._instance = value;
  }

  get isButtonClicked(): Ref<boolean> {
    return this._isButtonClicked;
  }

  set isButtonClicked(value: Ref<boolean>) {
    this._isButtonClicked = value;
  }
}

export const ButtonAction = ButtonActionKlass.getInstance();