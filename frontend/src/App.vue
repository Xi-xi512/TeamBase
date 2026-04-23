<template>
  <div class="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body {
  width: 100%;
  font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
  background: var(--theme-bg);
  color: var(--theme-text);
}

#app {
  width: 100%;
}

:root {
  /* Brand Colors */
  --theme-primary: #2563eb;
  --theme-primary-dark: #1d4ed8;
  --theme-primary-light: #dbeafe;
  --theme-success: #16a34a;
  --theme-warning: #d97706;
  --theme-danger: #dc2626;
  --theme-info: #0891b2;

  /* Surface & Text */
  --theme-bg: #f3f6fb;
  --theme-surface: #ffffff;
  --theme-text: #1f2937;
  --theme-muted: #6b7280;
  --theme-border: #dbe3ee;
  --theme-divider: #e8eef6;

  /* Shape */
  --theme-radius-sm: 8px;
  --theme-radius-md: 12px;
  --theme-radius-lg: 16px;

  /* Spacing (8px scale) */
  --space-1: 8px;
  --space-2: 12px;
  --space-3: 16px;
  --space-4: 24px;
  --space-5: 32px;
  --space-6: 40px;

  /* Elevation */
  --shadow-1: 0 1px 2px rgba(15, 23, 42, 0.06);
  --shadow-2: 0 4px 12px rgba(15, 23, 42, 0.08);
  --shadow-3: 0 12px 28px rgba(15, 23, 42, 0.14);

  --el-color-primary: var(--theme-primary);
  --el-color-primary-dark-2: #1e40af;
  --el-color-primary-light-3: #60a5fa;
  --el-color-primary-light-5: #93c5fd;
  --el-color-primary-light-7: #bfdbfe;
  --el-color-primary-light-8: #dbeafe;
  --el-color-primary-light-9: #eff6ff;
  --el-color-primary-light: var(--theme-primary-light);
  --el-color-success: var(--theme-success);
  --el-color-warning: var(--theme-warning);
  --el-color-danger: var(--theme-danger);
  --el-color-info: var(--theme-info);
  --el-border-radius-base: var(--theme-radius-sm);
  --el-bg-color: var(--theme-surface);
  --el-fill-color-light: #f8fbff;
  --el-border-color: var(--theme-border);
  --el-border-color-light: var(--theme-divider);
  --el-box-shadow-light: var(--shadow-1);
  --el-box-shadow: var(--shadow-2);
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.el-card {
  border-radius: var(--theme-radius-md);
  box-shadow: var(--shadow-1);
}

.el-tabs__item.is-active {
  font-weight: 600;
}
</style>
