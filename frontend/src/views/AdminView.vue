<template>
  <div class="admin-container">
    <div class="header">
      <h2>食堂管理后台</h2>
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>
    <div class="statistics">
      <h3>订餐统计报表</h3>
      <div v-if="loading">加载中...</div>
      <div v-else class="stats-content">
        <div class="stats-item">
          <span class="label">订餐总人数：</span>
          <span class="value">{{ statistics.totalStudents }} 人</span>
        </div>
        <div class="stats-item">
          <span class="label">订单总数：</span>
          <span class="value">{{ statistics.totalOrders }} 单</span>
        </div>
        <div class="dish-stats">
          <h4>各菜品需求量：</h4>
          <div v-if="Object.keys(statistics.dishStats).length === 0" class="no-data">
            暂无订单数据
          </div>
          <div v-else class="dish-list">
            <div v-for="(total, dishName) in statistics.dishStats" :key="dishName" class="dish-item">
              <span class="dish-name">{{ dishName }}</span>
              <span class="dish-total">
                {{ total % 1 === 0 ? total.toFixed(0) : total.toFixed(1) }} 份
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminView',
  data() {
    return {
      user: null,
      loading: true,
      statistics: {
        totalStudents: 0,
        totalOrders: 0,
        dishStats: {}
      }
    }
  },
  mounted() {
    this.getUserInfo()
    this.getStatistics()
  },
  methods: {
    getUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
        if (this.user.role !== 'admin') {
          this.$router.push('/')
        }
      } else {
        this.$router.push('/')
      }
    },
    async getStatistics() {
      try {
        const response = await fetch('/api/orders/statistics')
        const data = await response.json()
        if (data.success) {
          this.statistics = data.statistics
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      } finally {
        this.loading = false
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
.admin-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ddd;
}

.header h2 {
  color: #333;
}

.logout-btn {
  padding: 8px 16px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.logout-btn:hover {
  background-color: #d32f2f;
}

.statistics h3 {
  margin-bottom: 20px;
  color: #333;
}

.stats-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stats-item {
  margin-bottom: 15px;
  font-size: 16px;
}

.label {
  color: #666;
  margin-right: 10px;
}

.value {
  font-weight: bold;
  color: #333;
}

.dish-stats {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ddd;
}

.dish-stats h4 {
  margin-bottom: 15px;
  color: #333;
}

.no-data {
  color: #999;
  text-align: center;
  padding: 20px;
}

.dish-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}

.dish-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.dish-name {
  color: #333;
}

.dish-total {
  font-weight: bold;
  color: #4CAF50;
}
</style>
