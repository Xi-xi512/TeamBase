<template>
  <div class="student-page">
    <el-card shadow="never" class="header-card">
      <div class="header-row">
        <div>
          <h2>欢迎，{{ userName }}</h2>
          <p>在线订餐 · 明日备餐</p>
        </div>
        <el-button type="danger" plain @click="logout">退出登录</el-button>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="main-tabs" @tab-change="onMainTabChange">
      <el-tab-pane label="明日菜品预览" name="menu">
        <el-alert v-if="successBanner" :title="successBanner" type="success" show-icon :closable="false" />
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="panel-title">明日菜品（{{ menuDate || '-' }}）</div>
          </template>
          <div v-if="loading" class="muted">加载中...</div>
          <el-empty v-else-if="dishes.length === 0" description="暂无菜品" />
          <el-row v-else :gutter="16">
            <el-col v-for="dish in dishes" :key="dish.id" :xl="8" :md="12" :sm="24">
              <el-card shadow="hover" class="dish-card">
                <h4>{{ dish.name }}</h4>
                <p class="price">整份：¥{{ Number(dish.price).toFixed(2) }}</p>
                <p class="desc">{{ dish.description }}</p>
                <el-segmented
                  v-model="dishSelections[dish.id]"
                  :options="portionOptions(dish.price)"
                  @change="selectDish(dish.id, dishSelections[dish.id])"
                />
                <div class="card-action">
                  <el-button type="primary" @click="submitDishOrder(dish)">预约</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="订单中心" name="orders">
        <el-card shadow="hover">
          <el-tabs v-model="orderTab">
            <el-tab-pane label="明日订单" name="tomorrow">
              <div v-if="ordersLoading" class="muted">加载中...</div>
              <el-empty v-else-if="tomorrowOrders.length === 0" description="暂无明日订单" />
              <el-table v-else :data="tomorrowOrders" stripe>
                <el-table-column prop="orderId" label="订单号" min-width="130" />
                <el-table-column prop="dishName" label="菜品" min-width="120" />
                <el-table-column label="份量" width="90">
                  <template #default="{ row }">{{ row.portion === 'half' ? '半份' : '整份' }}</template>
                </el-table-column>
                <el-table-column prop="date" label="供餐日" width="120" />
                <el-table-column prop="createdAt" label="创建时间" min-width="170" />
                <el-table-column label="金额" width="100">
                  <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                  <template #default="{ row }">
                    <el-button link type="danger" @click="cancelOrder(row.orderId)">取消订单</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="历史订单" name="history">
              <div v-if="ordersLoading" class="muted">加载中...</div>
              <el-empty v-else-if="historyOrders.length === 0" description="暂无历史订单" />
              <el-table v-else :data="historyOrders" stripe>
                <el-table-column prop="orderId" label="订单号" min-width="130" />
                <el-table-column prop="dishName" label="菜品" min-width="120" />
                <el-table-column label="份量" width="90">
                  <template #default="{ row }">{{ row.portion === 'half' ? '半份' : '整份' }}</template>
                </el-table-column>
                <el-table-column prop="date" label="供餐日" width="120" />
                <el-table-column prop="createdAt" label="创建时间" min-width="170" />
                <el-table-column label="金额" width="100">
                  <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'StudentView',
  data() {
    return {
      user: null,
      userName: '',
      activeTab: 'menu',
      orderTab: 'tomorrow',
      dishes: [],
      loading: true,
      dishSelections: {},
      menuDate: '',
      successBanner: '',
      ordersLoading: false,
      myOrders: []
    }
  },
  computed: {
    tomorrowRef() {
      if (this.menuDate) return this.menuDate
      const d = new Date()
      d.setDate(d.getDate() + 1)
      return d.toISOString().slice(0, 10)
    },
    tomorrowOrders() {
      return this.myOrders.filter(o => o.date === this.tomorrowRef)
    },
    historyOrders() {
      return this.myOrders.filter(o => o.date !== this.tomorrowRef)
    }
  },
  async mounted() {
    if (!this.getUserInfo()) {
      this.loading = false
      return
    }
    await this.getDishes()
    await this.loadMyOrders()
  },
  methods: {
    onMainTabChange(name) {
      if (name === 'orders') {
        this.loadMyOrders()
      }
    },
    portionOptions(price) {
      return [
        { label: `整份 ¥${Number(price).toFixed(2)}`, value: 'whole' },
        { label: `半份 ¥${(Number(price) / 2).toFixed(2)}`, value: 'half' }
      ]
    },
    getUserInfo() {
      const userStr = localStorage.getItem('user')
      if (!userStr) {
        this.$router.push('/')
        return false
      }
      this.user = JSON.parse(userStr)
      if (this.user.role !== 'student') {
        this.$router.push('/')
        return false
      }
      this.userName = this.user.name
      return true
    },
    async loadMyOrders() {
      if (!this.user) return
      this.ordersLoading = true
      try {
        const response = await fetch(
          '/api/orders/mine?username=' + encodeURIComponent(this.user.username)
        )
        const data = await response.json()
        if (data.success) {
          this.myOrders = data.orders || []
        } else {
          ElMessage.error(data.message || '加载订单失败')
        }
      } catch (e) {
        ElMessage.error('加载订单失败')
      } finally {
        this.ordersLoading = false
      }
    },
    async getDishes() {
      try {
        const response = await fetch('/api/dishes')
        const data = await response.json()
        if (data.success) {
          this.dishes = data.dishes
          this.menuDate = data.menuDate || ''
          this.dishSelections = Object.fromEntries(this.dishes.map(d => [d.id, 'whole']))
        }
      } catch (error) {
        ElMessage.error('获取菜品失败')
      } finally {
        this.loading = false
      }
    },
    async submitDishOrder(dish) {
      this.successBanner = ''
      const portion = this.dishSelections[dish.id] || 'whole'
      try {
        const response = await fetch('/api/orders', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: this.user.username,
            dishId: dish.id,
            portion
          })
        })
        const text = await response.text()
        let data = null
        try {
          data = text ? JSON.parse(text) : null
        } catch (e) {
          console.error('提交订单响应非 JSON:', text)
        }
        if (!response.ok) {
          ElMessage.error(
            data && data.message
              ? data.message
              : '请求失败 HTTP ' + response.status + '：' + (text || '').slice(0, 200)
          )
          return
        }
        if (data && data.success) {
          const oid = data.order && data.order.orderId ? data.order.orderId : ''
          const amount = data.order && data.order.price != null ? Number(data.order.price).toFixed(2) : '-'
          const dishName = data.order && data.order.dishName ? data.order.dishName : dish.name
          const portionText = portion === 'half' ? '半份' : '整份'
          this.successBanner = `订单创建成功：${dishName} ${portionText}`
          await this.loadMyOrders()
          await ElMessageBox.alert(
            `菜品：${dishName}\n份量：${portionText}\n订单号：${oid || '-'}\n供餐日：${(data.order && data.order.date) || '-'}\n创建时间：${(data.order && data.order.createdAt) || '-'}\n金额：¥${amount}`,
            '订单创建成功',
            { confirmButtonText: '我知道了', type: 'success' }
          )
          this.activeTab = 'orders'
          this.orderTab = 'tomorrow'
        } else {
          ElMessage.error((data && data.message) || '下单失败，请稍后重试')
        }
      } catch (error) {
        ElMessage.error('提交订单失败，请稍后重试')
      }
    },
    async cancelOrder(orderId) {
      try {
        await ElMessageBox.confirm('确定取消该明日订单吗？', '取消确认', { type: 'warning' })
        const response = await fetch(
          '/api/orders/' + encodeURIComponent(orderId) + '?username=' + encodeURIComponent(this.user.username),
          { method: 'DELETE' }
        )
        const data = await response.json()
        if (data.success) {
          this.successBanner = '订单已取消'
          setTimeout(() => {
            this.successBanner = ''
          }, 5000)
          await this.loadMyOrders()
        } else {
          ElMessage.error(data.message || '取消失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          ElMessage.error('取消失败，请稍后重试')
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
.student-page {
  min-height: 100vh;
  background: transparent;
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
  padding-top: var(--space-1);
}

.main-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
}

.panel-card {
  margin-top: var(--space-2);
}

.dish-grid {
  margin-top: 8px;
}

.dish-card {
  margin-bottom: var(--space-3);
}

.dish-card h4 {
  margin: 0 0 8px;
}

.desc {
  color: var(--theme-muted);
  min-height: 40px;
}

.price {
  color: var(--theme-primary);
  font-weight: 600;
}

.card-action {
  margin-top: var(--space-2);
}

.muted {
  color: var(--theme-muted);
}

.confirm-card {
  margin-top: var(--space-2);
}

.panel-title {
  font-weight: 600;
  margin-bottom: var(--space-2);
}

.student-page :deep(.el-card) {
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-md);
}

.student-page :deep(.el-table th.el-table__cell) {
  background: var(--theme-primary-light);
  color: var(--theme-text);
}

@media (max-width: 768px) {
  .student-page {
    padding: var(--space-2);
    width: 100%;
  }

  .header-row h2 {
    font-size: 20px;
  }
}
</style>
