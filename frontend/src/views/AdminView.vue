<template>
  <div class="admin-page">
    <el-card shadow="never" class="header-card">
      <div class="header-row">
        <div>
          <h2>食堂管理后台</h2>
          <p>按供餐日维护菜单并查看统计报表</p>
        </div>
        <el-button type="danger" plain @click="logout">退出登录</el-button>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="main-tabs" @tab-change="onTabChange">
      <el-tab-pane label="订餐统计报表" name="stats">
        <el-card shadow="hover">
          <template #header>
            <div class="card-title">明日统计概览</div>
          </template>
          <el-skeleton :loading="statsLoading" animated :rows="4">
            <template #default>
              <el-row :gutter="16">
                <el-col :md="8" :sm="12" :xs="24">
                  <el-statistic title="订餐总人数" :value="statistics.totalStudents" suffix="人" />
                </el-col>
                <el-col :md="8" :sm="12" :xs="24">
                  <el-statistic title="订单总数" :value="statistics.totalOrders" suffix="单" />
                </el-col>
                <el-col :md="8" :sm="24" :xs="24">
                  <el-statistic title="供餐日" :value="statistics.servingDate || '-'" />
                </el-col>
              </el-row>
              <el-divider />
              <el-table :data="dishStatsRows" stripe style="width: 100%">
                <el-table-column prop="dishName" label="菜品" />
                <el-table-column prop="totalText" label="需求量" width="140" />
              </el-table>
            </template>
          </el-skeleton>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="每日菜品管理" name="dishes">
        <el-row :gutter="16">
          <el-col :md="15" :xs="24">
            <el-card shadow="hover">
              <template #header>
                <div class="toolbar">
                  <div class="card-title">菜品列表</div>
                  <div class="toolbar-actions">
                    <el-date-picker
                      v-model="dishFilterDate"
                      type="date"
                      value-format="YYYY-MM-DD"
                      placeholder="筛选供餐日"
                      clearable
                      @change="loadAdminDishes"
                    />
                    <el-button @click="clearDishFilter">显示全部</el-button>
                    <el-button type="primary" plain @click="loadAdminDishes">刷新</el-button>
                  </div>
                </div>
              </template>
              <el-table v-loading="dishLoading" :data="adminDishes" stripe style="width:100%">
                <el-table-column prop="id" label="编号" width="90" />
                <el-table-column prop="name" label="名称" width="120" />
                <el-table-column label="整份价" width="100">
                  <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
                </el-table-column>
                <el-table-column prop="menuDate" label="供餐日" width="130" />
                <el-table-column prop="description" label="描述" show-overflow-tooltip />
                <el-table-column label="操作" width="140" fixed="right">
                  <template #default="{ row }">
                    <el-button link type="primary" @click="editDish(row)">编辑</el-button>
                    <el-button link type="danger" @click="removeDish(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>

          <el-col :md="9" :xs="24">
            <el-card shadow="hover">
              <template #header>
                <div class="card-title">{{ editingId ? '编辑菜品' : '新增菜品' }}</div>
              </template>
              <el-form :model="dishForm" label-position="top">
                <el-form-item label="编号">
                  <el-input v-model="dishForm.id" :disabled="!!editingId" placeholder="如 D010" />
                </el-form-item>
                <el-form-item label="名称">
                  <el-input v-model="dishForm.name" placeholder="菜名" />
                </el-form-item>
                <el-form-item label="整份价">
                  <el-input-number v-model="dishForm.price" :min="0" :precision="2" style="width:100%" />
                </el-form-item>
                <el-form-item label="供餐日">
                  <el-date-picker
                    v-model="dishForm.menuDate"
                    type="date"
                    value-format="YYYY-MM-DD"
                    placeholder="选择供餐日"
                    style="width:100%"
                  />
                </el-form-item>
                <el-form-item label="描述">
                  <el-input v-model="dishForm.description" type="textarea" :rows="3" />
                </el-form-item>
                <div class="form-actions">
                  <el-button type="primary" @click="saveDish">保存</el-button>
                  <el-button v-if="editingId" @click="resetDishForm">取消编辑</el-button>
                </div>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminView',
  data() {
    return {
      user: null,
      activeTab: 'stats',
      statsLoading: true,
      statistics: {
        totalStudents: 0,
        totalOrders: 0,
        dishStats: {},
        servingDate: ''
      },
      dishLoading: false,
      adminDishes: [],
      dishFilterDate: '',
      dishForm: {
        id: '',
        name: '',
        price: 0,
        description: '',
        menuDate: ''
      },
      editingId: null
    }
  },
  computed: {
    dishStatsRows() {
      return Object.entries(this.statistics.dishStats || {}).map(([dishName, total]) => ({
        dishName,
        totalText: `${Number(total).toFixed(total % 1 === 0 ? 0 : 1)} 份`
      }))
    }
  },
  async mounted() {
    if (!this.getUserInfo()) {
      this.statsLoading = false
      return
    }
    await this.getStatistics()
  },
  methods: {
    getUserInfo() {
      const userStr = localStorage.getItem('user')
      if (!userStr) {
        this.$router.push('/')
        return false
      }
      this.user = JSON.parse(userStr)
      if (this.user.role !== 'admin') {
        this.$router.push('/')
        return false
      }
      return true
    },
    async getStatistics() {
      this.statsLoading = true
      try {
        const response = await fetch('/api/orders/statistics?username=' + encodeURIComponent(this.user.username))
        const data = await response.json()
        if (data.success) {
          this.statistics = data.statistics
        } else {
          ElMessage.error(data.message || '获取统计数据失败')
          this.$router.push('/')
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      } finally {
        this.statsLoading = false
      }
    },
    onTabChange(tabName) {
      if (tabName === 'dishes') {
        this.loadAdminDishes()
      }
    },
    clearDishFilter() {
      this.dishFilterDate = ''
      this.loadAdminDishes()
    },
    async loadAdminDishes() {
      if (!this.user) return
      this.dishLoading = true
      try {
        let url = '/api/admin/dishes?username=' + encodeURIComponent(this.user.username)
        if (this.dishFilterDate) {
          url += '&menuDate=' + encodeURIComponent(this.dishFilterDate)
        }
        const response = await fetch(url)
        const data = await response.json()
        if (data.success) {
          this.adminDishes = data.dishes || []
        } else {
          ElMessage.error(data.message || '加载菜品失败')
        }
      } catch (e) {
        console.error(e)
        ElMessage.error('加载菜品失败')
      } finally {
        this.dishLoading = false
      }
    },
    editDish(d) {
      this.editingId = d.id
      this.dishForm = {
        id: d.id,
        name: d.name,
        price: d.price,
        description: d.description || '',
        menuDate: d.menuDate || ''
      }
    },
    resetDishForm() {
      this.editingId = null
      this.dishForm = { id: '', name: '', price: 0, description: '', menuDate: '' }
    },
    async saveDish() {
      if (!this.user) return
      try {
        const response = await fetch(
          '/api/admin/dishes?username=' + encodeURIComponent(this.user.username),
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              id: this.dishForm.id,
              name: this.dishForm.name,
              price: Number(this.dishForm.price),
              description: this.dishForm.description || '',
              menuDate: this.dishForm.menuDate
            })
          }
        )
        const data = await response.json()
        if (data.success) {
          ElMessage.success(data.message || '保存成功')
          this.editingId = null
          this.dishForm = { id: '', name: '', price: 0, description: '', menuDate: '' }
          await this.loadAdminDishes()
        } else {
          ElMessage.error(data.message || '保存失败')
        }
      } catch (e) {
        ElMessage.error('保存失败，请检查网络')
      }
    },
    async removeDish(d) {
      if (!this.user) return
      try {
        await ElMessageBox.confirm(`确定删除「${d.name}」吗？`, '删除确认', { type: 'warning' })
        const response = await fetch(
          '/api/admin/dishes/' + encodeURIComponent(d.id) + '?username=' + encodeURIComponent(this.user.username),
          { method: 'DELETE' }
        )
        const data = await response.json()
        if (data.success) {
          ElMessage.success(data.message || '已删除')
          if (this.editingId === d.id) {
            this.resetDishForm()
          }
          await this.loadAdminDishes()
        } else {
          ElMessage.error(data.message || '删除失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    },
    logout() {
      localStorage.removeItem('user')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.admin-page {
  padding: var(--space-4);
  width: min(1680px, 98vw);
  margin: 0 auto;
}

.header-card {
  margin-bottom: var(--space-3);
  border-radius: var(--theme-radius-lg);
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.header-row h2 {
  margin: 0;
  font-size: 24px;
  color: var(--theme-text);
}

.header-row p {
  margin: 6px 0 0;
  color: var(--theme-muted);
}

.main-tabs :deep(.el-tabs__content) {
  padding-top: var(--space-2);
}

.main-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
  color: var(--theme-text);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  flex-wrap: wrap;
}

.form-actions {
  display: flex;
  gap: var(--space-2);
}

.admin-page :deep(.el-card) {
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-md);
}

.admin-page :deep(.el-table th.el-table__cell) {
  background: var(--theme-primary-light);
  color: var(--theme-text);
}

.admin-page :deep(.el-table__row:hover > td.el-table__cell) {
  background: #f8fbff !important;
}

@media (max-width: 768px) {
  .admin-page {
    padding: var(--space-2);
    width: 100%;
  }
}
</style>
