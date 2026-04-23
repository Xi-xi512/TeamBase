<template>
  <div class="login-page">
    <el-card class="login-card" shadow="hover">
      <div class="brand">
        <h1>校园食堂订餐系统</h1>
      </div>
      <el-form label-position="top" @submit.prevent>
        <el-form-item label="账号">
          <el-input v-model="username" placeholder="请输入账号" @input="clearError" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="password" type="password" show-password placeholder="请输入密码" @input="clearError" />
        </el-form-item>
      </el-form>
      <el-alert v-if="errorMsg" :title="errorMsg" type="error" :closable="false" show-icon />
      <el-button type="primary" class="login-btn" @click="login">登录</el-button>
      <el-button text class="test-btn" @click="showTestAccounts">查看测试账号</el-button>
      <el-alert
        v-if="testAccountsVisible"
        class="test-box"
        title="测试账号"
        type="info"
        :closable="false"
      >
        <pre>{{ testAccounts }}</pre>
      </el-alert>
    </el-card>
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
      testAccountsVisible: false,
      errorMsg: ''
    }
  },
  methods: {
    clearError() {
      this.errorMsg = ''
    },
    async login() {
      this.errorMsg = ''
      if (!this.username.trim() || !this.password) {
        this.errorMsg = '请输入账号和密码'
        return
      }
      try {
        const response = await fetch('/api/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ username: this.username, password: this.password })
        })
        const text = await response.text()
        let data = null
        try {
          data = text ? JSON.parse(text) : null
        } catch (e) {
          console.error('登录响应非 JSON', text)
        }
        if (!response.ok) {
          this.errorMsg = (data && data.message) || ('登录请求失败 (HTTP ' + response.status + ')')
          return
        }
        if (data && data.success) {
          const user = data.user
          localStorage.setItem('user', JSON.stringify(user))
          if (user.role === 'admin') {
            this.$router.push('/admin')
          } else {
            this.$router.push('/student')
          }
        } else {
          this.errorMsg = (data && data.message) || '账号或密码错误，请重试'
        }
      } catch (error) {
        console.error('登录失败:', error)
        this.errorMsg = '网络异常，请稍后重试'
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
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: var(--space-4);
}

.login-card {
  width: min(520px, 96vw);
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-lg);
  box-shadow: var(--shadow-3);
}

.brand {
  margin-bottom: var(--space-4);
}

.brand h1 {
  margin: 0;
  font-size: 28px;
  color: var(--theme-text);
}

.login-btn {
  width: 100%;
  margin-top: 6px;
}

.test-btn {
  width: 100%;
  margin-top: 8px;
}

.test-box {
  margin-top: 12px;
}

pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: inherit;
  color: var(--theme-text);
}

@media (max-width: 768px) {
  .brand h1 {
    font-size: 22px;
  }

  .login-page {
    padding: var(--space-2);
  }
}
</style>
