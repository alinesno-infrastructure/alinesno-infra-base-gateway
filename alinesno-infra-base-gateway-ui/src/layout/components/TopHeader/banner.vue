<template>
  <div>
    <div class="header-logo-bar" to="/index">
      <div class="header-logo" v-if="enableLogo" @click="enterDomain">
        <img :src="saasLogoUrl" alt="" />
      </div>
      <a :title="saasTitle" target="_self" class="header-logo-label">
        <span>{{ saasTitle }} </span>
      </a>
      <div v-if="saasUrl" class="dashboard-home" @click="dashboardHome()">
        <i class="fa-solid fa-house"></i> 工作台
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AIPLogo from '@/assets/logo/logo.png'

const saasTitle = ref('AIP智能设施')
const enableLogo = ref(true)
const saasUrl = ref('http://alinesno-infra-plat-console-ui.beta.base.infra.linesno.com')
const saasLogoUrl = ref(AIPLogo)
const displayUrl = ref('')
const domainName = ref(null)

function dashboardHome() {
  // 在 setup 中使用 ref 时在 JS 里需访问 .value
  window.location.href = saasUrl.value
}

function enterDomain() {
  if (domainName.value) {
    window.open(domainName.value)
  }
}
</script>