<template>
  <div>
    <el-row class="acp-dashboard-panel" :gutter="20">
      <el-col class="panel-col" :span="19">
        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-link"></i> 网关平台能力</div>
          </div>
          <div class="panel-body" style="height: auto;position: relative;">
              <div class="direct-box">
                <ul>
                  <li class="box-item" v-for="item in advantages" :key="item">
                    <div class="dire-panel">
                      <div class="panel-title">
                        <i class="dire-panel-icon" :class="item.icon" :alt="item.name" />
                        {{ item.title }} 
                      </div>
                      <div class="panel-describe">
                        {{ item.description }}
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
          </div>
        </div>
      </el-col>
      <el-col :span="5">
        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-link"></i> 应用场景</div>
          </div>
          <div class="panel-body" style="height: auto;position: relative;">
            <div class="acp-app-list">
              <ul>
                <li class="app-items" style="width:100%" v-for="item in gatewayApplicationScenarios" :key="item">
                  <div class="app-info">
                    <div class="app-item-title">
                      <i :class="item.icon" :alt="item.name" />
                        {{ item.name }}
                    </div>
                    <div class="app-item desc">{{ item.description }}</div>
                  </div>
                </li>
              </ul>
            </div>
          </div>

        </div>
      </el-col>
    </el-row>

  </div>
</template>

<script setup>

const advantages = [
  { icon: 'fa-solid fa-shield-alt', title: '安全更可靠', description: '采用先进的安全策略和技术，提供强大的身份验证、授权和加密功能，确保微服务通信的安全性。' },
  { icon: 'fa-solid fa-chart-line', title: '路由更智能', description: '内置智能路由算法，根据请求特征动态选择最优路径，保证高并发下的高效和稳定的服务访问。' },
  { icon: 'fa-solid fa-clipboard-list', title: '配置更灵活', description: '支持多种配置管理方式，包括但不限于图形界面、API接口，让配置和服务治理更加直观和便捷。' },
  { icon: 'fa-solid fa-chart-bar', title: '性能监控全面', description: '提供对微服务调用链路的全维度性能监控，从流量到响应时间，助力实时掌握系统运行状态。' },
  { icon: 'fa-solid fa-cogs', title: '插件化扩展', description: '通过插件机制，可以轻松集成第三方服务或自定义功能，满足多样化的业务需求，增强平台的适应性和灵活性。' },
  { icon: 'fa-solid fa-users-cog', title: '运维更省心', description: '自动化运维工具简化了日常管理和维护工作，降低了运维成本，使团队能够专注于核心业务的发展。' }
];

const gatewayApplicationScenarios = [
  { icon: 'fa-solid fa-code-branch', name: '微服务间通信', description: '为微服务架构中的服务提供安全、可靠的通信通道，管理服务间的调用流量，确保高并发情况下的稳定性和响应速度。' },
  { icon: 'fa-solid fa-arrows-turn-right', name: '流量路由', description: '根据预定义规则或实时状态动态地分配请求到不同的后端服务实例，支持负载均衡和故障转移，提高系统的可用性。' },
  { icon: 'fa-solid fa-chart-line', name: '性能监控与分析', description: '收集并分析通过网关的流量数据，监控API性能指标，如响应时间、成功率等，帮助识别和优化性能瓶颈。' },
];


</script>