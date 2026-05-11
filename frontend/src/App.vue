<template>
  <a href="#main-content" class="skip-link">{{ $t('common.skipToContent') }}</a>
  <Toast position="top-left" />
  <ConfirmDialog />

  <RouterView v-if="route.path === '/login'" />

  <div v-else class="app-shell">
    <aside class="sidebar">
      <!-- Brand -->
      <div class="sidebar-brand">
        <span class="brand-icon pi pi-book" />
        <h1 class="brand">الحافظ</h1>
      </div>

      <!-- Nav -->
      <nav class="nav" :aria-label="$t('nav.sidebarLabel')">
        <template v-for="group in navGroups" :key="group.label ?? 'top'">
          <p v-if="group.label" class="nav-group-label">{{ $t(group.label) }}</p>
          <RouterLink
            v-for="item in group.items"
            :key="item.to"
            :to="item.to"
            class="nav-link"
          >
            <span :class="['nav-icon', 'pi', item.icon]" />
            <span class="nav-label">{{ $t(item.label) }}</span>
          </RouterLink>
        </template>
      </nav>

      <!-- User chip + logout -->
      <div class="sidebar-footer">
        <RouterLink to="/profile" class="user-chip">
          <Avatar
            :label="userInitial"
            shape="circle"
            class="user-avatar"
          />
          <span class="user-name">{{ userName }}</span>
        </RouterLink>
        <Button
          icon="pi pi-sign-out"
          text
          rounded
          class="logout-btn"
          :aria-label="$t('login.logout')"
          @click="logout"
        />
      </div>
    </aside>

    <main id="main-content" class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter, RouterLink, RouterView } from "vue-router";
import { useI18n } from "vue-i18n";
import Avatar from "primevue/avatar";
import Button from "primevue/button";
import Toast from "primevue/toast";
import ConfirmDialog from "primevue/confirmdialog";

const route  = useRoute();
const router = useRouter();
const { t }  = useI18n();

const navGroups = [
  {
    items: [
      { to: "/reports",     icon: "pi-chart-bar", label: "nav.reports"     },
      { to: "/recitations", icon: "pi-book",       label: "nav.recitations" },
    ],
  },
  {
    label: "nav.group.masterData",
    items: [
      { to: "/students",      icon: "pi-users",   label: "nav.students"     },
      { to: "/sheikhs",       icon: "pi-user",    label: "nav.sheikhs"      },
      { to: "/levels",        icon: "pi-list",    label: "nav.levels"       },
      { to: "/groups",        icon: "pi-sitemap", label: "nav.groups"       },
      { to: "/mistake-types", icon: "pi-tag",     label: "nav.mistakeTypes" },
    ],
  },
  {
    label: "nav.group.admin",
    items: [
      { to: "/users", icon: "pi-shield", label: "nav.users" },
    ],
  },
];

const user        = computed(() => JSON.parse(localStorage.getItem("user") || "{}"));
const userName    = computed(() => user.value.name || "—");
const userInitial = computed(() => (user.value.name || "؟")[0]);

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  router.push("/login");
}
</script>

<style scoped>
/* ── Skip link ───────────────────────────────────────────────────────────── */
.skip-link {
  position: absolute;
  inset-block-start: var(--space-4);
  inset-inline-start: var(--space-4);
  transform: translateY(-200%);
  background: var(--color-primary-600);
  color: #ffffff;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-md);
  font-weight: 600;
  font-size: var(--text-sm);
  text-decoration: none;
  z-index: 9999;
  transition: transform var(--transition-fast);
}

.skip-link:focus {
  transform: translateY(0);
}

/* ── Nav group labels ────────────────────────────────────────────────────── */
.nav-group-label {
  margin: var(--space-3) 0 0;
  padding: var(--space-3) var(--space-4) var(--space-1);
  font-size: var(--text-xs);
  font-weight: 600;
  color: rgba(255, 255, 255, 0.38);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

/* ── Sidebar brand ───────────────────────────────────────────────────────── */
.sidebar-brand {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding-bottom: var(--space-4);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.brand-icon {
  font-size: 1.5rem;
  color: rgba(255, 255, 255, 0.7);
}

.brand {
  font-family: var(--font-serif);
  font-size: var(--text-2xl);
  margin: 0;
  color: #f5f8ff;
  line-height: var(--leading-tight);
}

/* ── Nav links ───────────────────────────────────────────────────────────── */
.nav {
  display: grid;
  gap: var(--space-1);
  flex: 1;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid transparent;
  color: var(--color-sidebar-text);
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--transition-normal);
  text-decoration: none;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.13);
  color: #ffffff;
}

.nav-link.router-link-active {
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.16);
  color: var(--color-sidebar-text-active);
  font-weight: 600;
}

.nav-icon {
  font-size: 1rem;
  width: 18px;
  text-align: center;
  flex-shrink: 0;
  opacity: 0.8;
}

.nav-link.router-link-active .nav-icon {
  opacity: 1;
}

.nav-label {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ── Sidebar footer ──────────────────────────────────────────────────────── */
.sidebar-footer {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding-top: var(--space-4);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-chip {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  flex: 1;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f0f6ff;
  text-decoration: none;
  transition: background var(--transition-normal);
  min-width: 0;
}

.user-chip:hover {
  background: rgba(255, 255, 255, 0.14);
}

.user-avatar {
  flex-shrink: 0;
  font-size: var(--text-sm);
  font-weight: 700;
  background: rgba(255, 255, 255, 0.2) !important;
  color: #ffffff !important;
  width: 32px !important;
  height: 32px !important;
}

.user-name {
  font-size: var(--text-sm);
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-btn {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-normal);
  padding: 0;
  font-size: 0.95rem;
}

.logout-btn:hover {
  background: rgba(220, 38, 38, 0.3);
  border-color: rgba(220, 38, 38, 0.4);
  color: #fca5a5;
  transform: none;
}

/* ── Responsive ──────────────────────────────────────────────────────────── */
@media (max-width: 960px) {
  .sidebar-brand {
    padding-bottom: 0;
    border-bottom: none;
    padding-inline-end: var(--space-3);
    border-inline-end: 1px solid rgba(255, 255, 255, 0.1);
    flex-shrink: 0;
  }

  .nav {
    display: flex;
    flex-direction: row;
    overflow-x: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
    flex: 1;
    gap: var(--space-1);
    align-items: center;
  }

  .nav::-webkit-scrollbar {
    display: none;
  }

  .nav-group-label {
    display: none;
  }

  .nav-link {
    white-space: nowrap;
    flex-shrink: 0;
    padding: var(--space-2) var(--space-3);
  }

  .sidebar-footer {
    padding-top: 0;
    border-top: none;
    padding-inline-start: var(--space-3);
    border-inline-start: 1px solid rgba(255, 255, 255, 0.1);
    flex-shrink: 0;
  }
}

@media (max-width: 600px) {
  .brand {
    display: none;
  }

  .nav-label {
    display: none;
  }

  .nav-link {
    padding: var(--space-2);
    justify-content: center;
  }

  .user-name {
    display: none;
  }
}
</style>
