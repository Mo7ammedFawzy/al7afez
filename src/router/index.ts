import {createRouter, createWebHistory, type RouteRecordRaw,} from "vue-router"

export const routes: RouteRecordRaw[] = [
    {
        name: "home",
        path: "/",
        component: () => import("@/views/index.vue")
    },
]

export const routeWithoutComponents = (): Pick<RouteRecordRaw, "name" | "path">[] =>
    routes.map(({component, ...route}) => route)

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

export default router;
