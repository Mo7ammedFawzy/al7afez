import {defineStore} from "pinia";

export const useStore = defineStore("use-store", {
    state: () => ({
        counter: 0
    }),
    actions: {
        setCounter(value: number) {
            this.counter = value;
        }
    },
    getters: {
        getMultipleCounter(state): number {
            return state.counter + 2
        }
    }
})