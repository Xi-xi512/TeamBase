<template>
  <div class="login-container">
    <div class="login-form">
      <h2>校园食堂订餐系统</h2>
      <div class="form-group">
        <label for="username">账号</label>
        <input type="text" id="username" v-model="username" placeholder="请输入账号">
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input type="password" id="password" v-model="password" placeholder="请输入密码">
      </div>
      <button class="login-btn" @click="login">登录</button>
      <div class="test-accounts" @click="showTestAccounts">
        查看测试账号
      </div>
      <div v-if="testAccountsVisible" class="test-accounts-list">
        <pre>{{ testAccounts }}</pre>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      username: '',
      password: '',
      testAccounts: '',
      testAccountsVisible: false
    }
  },
  methods: {
    async login() {
      try {
        const response = await fetch('/api/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ username: this.username, password: this.password })
        })
        const data = await response.json()
        if (data.success) {
          const user = data.user
          localStorage.setItem('user', JSON.stringify(user))
          if (user.role === 'admin') {
            this.$router.push('/admin')
          } else {
            this.$router.push('/student')
          }
        } else {
          alert(data.message)
        }
      } catch (error) {
        console.error('登录失败:', error)
        alert('登录失败，请稍后重试')
      }
    },
    async showTestAccounts() {
      try {
        const response = await fetch('/api/login/test-accounts')
        const data = await response.json()
        if (data.success) {
          this.testAccounts = data.accounts
          this.testAccountsVisible = true
        }
      } catch (error) {
        console.error('获取测试账号失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  background-color: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}

.login-btn:hover {
  background-color: #45a049;
}

.test-accounts {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  text-decoration: underline;
}

.test-accounts-list {
  margin-top: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  font-size: 14px;
}

pre {
  white-space: pre-wrap;
  font-family: Arial, sans-serif;
}
</style>
