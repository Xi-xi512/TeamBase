<template>
  <div class="student-container">
    <div class="header">
      <h2>欢迎，{{ userName }}</h2>
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>
    <div class="dish-list">
      <h3>明日菜品预览</h3>
      <div v-if="loading">加载中...</div>
      <div v-else-if="dishes.length === 0">暂无菜品</div>
      <div v-else class="dishes">
        <div v-for="dish in dishes" :key="dish.id" class="dish-item">
          <h4>{{ dish.name }}</h4>
          <p class="price">整份：¥{{ dish.price.toFixed(2) }}</p>
          <p class="description">{{ dish.description }}</p>
          <div class="portion-select">
            <button 
              class="portion-btn" 
              :class="{ active: selectedDish === dish.id && selectedPortion === 'whole' }"
              @click="selectDish(dish.id, 'whole')"
            >
              整份 (¥{{ dish.price.toFixed(2) }})
            </button>
            <button 
              class="portion-btn" 
              :class="{ active: selectedDish === dish.id && selectedPortion === 'half' }"
              @click="selectDish(dish.id, 'half')"
            >
              半份 (¥{{ (dish.price / 2).toFixed(2) }})
            </button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="selectedDish" class="order-confirm">
      <h3>确认订单</h3>
      <p>菜品：{{ selectedDishName }}</p>
      <p>份量：{{ selectedPortion === 'whole' ? '整份' : '半份' }}</p>
      <p>价格：¥{{ selectedPrice.toFixed(2) }}</p>
      <button class="order-btn" @click="submitOrder">提交订单</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StudentView',
  data() {
    return {
      user: null,
      userName: '',
      dishes: [],
      loading: true,
      selectedDish: null,
      selectedPortion: 'whole',
      selectedDishName: '',
      selectedPrice: 0
    }
  },
  mounted() {
    this.getUserInfo()
    this.getDishes()
  },
  methods: {
    getUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
        this.userName = this.user.name
      } else {
        this.$router.push('/')
      }
    },
    async getDishes() {
      try {
        const response = await fetch('/api/dishes')
        const data = await response.json()
        if (data.success) {
          this.dishes = data.dishes
        }
      } catch (error) {
        console.error('获取菜品失败:', error)
      } finally {
        this.loading = false
      }
    },
    selectDish(dishId, portion) {
      this.selectedDish = dishId
      this.selectedPortion = portion
      const dish = this.dishes.find(d => d.id === dishId)
      if (dish) {
        this.selectedDishName = dish.name
        this.selectedPrice = portion === 'whole' ? dish.price : dish.price / 2
      }
    },
    async submitOrder() {
      try {
        const response = await fetch('/api/orders', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: this.user.username,
            dishId: this.selectedDish,
            portion: this.selectedPortion
          })
        })
        const data = await response.json()
        if (data.success) {
          alert('订单提交成功！')
          this.selectedDish = null
          this.selectedPortion = 'whole'
          this.selectedDishName = ''
          this.selectedPrice = 0
        } else {
          alert(data.message)
        }
      } catch (error) {
        console.error('提交订单失败:', error)
        alert('提交订单失败，请稍后重试')
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
.student-container {
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

.dish-list h3 {
  margin-bottom: 20px;
  color: #333;
}

.dishes {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.dish-item {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.dish-item h4 {
  margin-bottom: 10px;
  color: #333;
}

.price {
  color: #4CAF50;
  font-weight: bold;
  margin-bottom: 10px;
}

.description {
  color: #666;
  margin-bottom: 15px;
  font-size: 14px;
}

.portion-select {
  display: flex;
  gap: 10px;
}

.portion-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  cursor: pointer;
  transition: all 0.3s;
}

.portion-btn.active {
  background-color: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.order-confirm {
  margin-top: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.order-confirm h3 {
  margin-bottom: 15px;
  color: #333;
}

.order-confirm p {
  margin-bottom: 10px;
  color: #666;
}

.order-btn {
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.order-btn:hover {
  background-color: #45a049;
}
</style>
