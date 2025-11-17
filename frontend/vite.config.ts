import {fileURLToPath, URL} from 'node:url'
import {defineConfig} from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";
import ui from "@nuxt/ui/vite";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        vueDevTools({
            launchEditor: "idea"
        }),
        ui({
          ui: {
            colors: {
              primary: 'blue-ribbon',
              neutral: 'zinc',
              secondary: "rose"
            },
            button: {
              base: "dark:text-white capitalize cursor-pointer"
            },
            skeleton: {
              base: 'dark:bg-gray-800 bg-gray-200'
            }
          },
        }),
    ],
    resolve: {
        alias: {
            "@": fileURLToPath(new URL("./src", import.meta.url))
        }
    },
    build: {
        sourcemap: true
    }
});
