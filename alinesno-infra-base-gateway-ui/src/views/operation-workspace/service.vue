<template>
  <div>
    <el-row class="acp-dashboard-panel" :gutter="20">

      <el-col :span="19">

        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-user-nurse"></i> 接口调用状态</div>
          </div>
          <div class="panel-body acp-height-auto">
        <div class="box-header">
          <el-row>
            <el-col :span="4">
              <div class="item-box">
                <ul>
                  <li
                    class="item-box-info"
                    :class="index === 0 ? 'active' : ''"
                    v-for="(item, index) in components"
                    :key="index"
                  >
                    <div class="item-status">
                      <div
                        class="server-desc"
                        style="margin-top: 10px; margin-left: 10px"
                      >
                        <i :class="item.icon"></i>
                      </div>
                    </div>
                    <div class="status-info">
                      <div class="item-text">{{ item.title }}</div>
                      <div class="item-num">
                        {{ item.usage }}
                        <span class="total-num">{{ item.total }}</span>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </el-col>
            <el-col :span="20">
              <div class="sidecard-bar">
                <div
                  id="echarts-bar2-chart"
                  :style="{ width: '100%', height: '250px' }"
                ></div>
              </div>
            </el-col>
          </el-row>
        </div>
          </div>
        </div>

      </el-col>

      <el-col :span="5">
        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-user-nurse"></i> 接口调用统计</div>
          </div>
          <div class="panel-body acp-height-auto">
            <ul class="panel-item-text">
              <li style="width:calc(50% - 20px);margin:10px;padding:10px;" v-for="item in opertionAssets" :key="item.id">
                <div class="item-health-box">
                  <div class="item-health-title">{{ item.title }}</div>
                  <div class="item-health-count">{{ item.count }}</div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </el-col>

    </el-row>
  </div>
</template>

<script setup>

import * as echarts from "echarts";

/// 声明定义一下echart
const echart = echarts;

const opertionAssets = ref([
  {id:'1' , title:'运维脚本' , count:45} ,
  {id:'2' , title:'服务资源' , count:145} ,
  {id:'3' , title:'持续集成' , count:65} ,
  {id:'4' , title:'自动配置' , count:85} ,
  {id:'5' , title:'配置管理' , count:45} ,
])

const components = ref([
  {
    icon: "fas fa-tools",
    title: "API 服务器",
    total: "延迟",
    usage: "请求",
  },
  {
    icon: "fas fa-shipping-fast",
    title: "API 服务器",
    total: "速率",
    usage: "请求",
  },
  { icon: "fas fa-route", title: "调度器", total: "速率", usage: "调度" },
  {
    icon: "fab fa-firefox",
    title: "调度器",
    total: "次数",
    usage: "调度",
  },
]) ; 

const apps = ref([
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/d88319dfa5d204f019b4284149886c59-7d586ea82f792b61a8c87de60565133d.svg' , name:'自动化操作服务' , desc:'With Route 53 (3 分钟)'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/9da5a168cf8194c8ee5ed192a443d563-674375b53bc8ae94f48cfdb5c81e8363.svg' , name:'分布式配置中心' , desc:'With Route 53 (3 分钟)'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/a5ffe5487f62ef75d8e5cf78c18525a5-d4867f9d4adcd749f0c5aff987232847.svg' , name:'审计日志服务' , desc:'With Route 53 (3 分钟)'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/6e9e16955bd76c1d3a9996e0d6d3ad15-04cfc8c62f597690680d948b366980cf.svg' , name:'持续集成服务' , desc:'With Route 53 (3 分钟)'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/d88319dfa5d204f019b4284149886c59-7d586ea82f792b61a8c87de60565133d.svg' , name:'容器云管理服务' , desc:'With EC2 (2 分钟)'},
  {icon:'https://d1by4p17n947rt.cloudfront.net/icon/3da5e8169d2f1426f99fbef54575fe96-6382cb2dfdd2f74c99bc8a64a338358e.svg' , name:'一体化安全感触服务' , desc:'With Route 53 (3 分钟)'},
]);


function drawBar2() {
      let barChart = echart.init(
        document.getElementById("echarts-bar2-chart")
      );

      var lineOption = {
        title: {
          text: "请求延迟(ms)",
        },
        tooltip: {
          trigger: "axis",
        },
        legend: {
          data: ["访问流量", "访问IP"],
        },
        grid: {
          x: 40,
          x2: 40,
          y2: 24,
        },
        calculable: true,
        xAxis: [
          {
            type: "category",
            boundaryGap: false,
            data: [
              "8",
              "9",
              "10",
              "11",
              "12",
              "13",
              "14",
              "15",
              "16",
              "17",
              "18",
              "19",
              "20",
              "21",
              "22",
              "23",
              "24",
              "25",
              "26",
              "27",
              "28",
              "1",
              "2",
              "3",
              "4",
              "5",
              "6",
              "7",
              "8",
              "9",
              "10",
              "11",
              "12",
              "13",
              "14",
              "15",
              "16",
              "17",
              "18",
              "19",
              "20",
              "21",
              "22",
              "23",
              "24",
              "25",
              "26",
              "27",
              "28",
              "8",
              "9",
              "10",
              "11",
              "12",
              "13",
              "14",
              "15",
              "16",
              "17",
              "18",
              "19",
              "20",
              "21",
              "8",
              "9",
              "10",
              "11",
              "12",
              "13",
              "14",
              "15",
              "16",
              "17",
              "18",
              "19",
              "20",
              "21",
              "22",
              "23",
              "24",
              "25",
              "26",
              "27",
              "28",
              "8",
              "9",
              "10",
              "11",
              "12",
              "13",
              "14",
              "15",
              "16",
              "17",
              "18",
              "19",
              "20",
              "21",
              "22",
              "23",
              "24",
              "25",
              "26",
              "27",
              "28",
              "22",
              "23",
              "24",
              "25",
              "26",
              "27",
              "28",
            ],
          },
        ],
        yAxis: [
          {
            type: "value",
            axisLabel: {
              formatter: "{value} K",
            },
          },
        ],
        series: [
          {
            name: "访问流量",
            type: "line",
            smooth: true,
            data: [
              11, 10, 12, 13, 5, 13, 8, 11, 9, 15, 8, 12, 8, 8, 11, 10, 12, 13,
              5, 13, 8, 11, 9, 15, 8, 12, 8, 8, 11, 10, 15, 13, 5, 13, 8, 11,
              10, 12, 13, 5, 13, 8, 11, 9, 15, 8, 12, 8, 8, 11, 10, 15, 13, 5,
              13, 8, 11, 9, 15, 8, 12, 8, 8, 11, 10, 12, 13, 5, 13, 8, 11, 9,
              15, 8, 12, 8, 8, 11, 10, 15, 13, 5, 13, 8, 11, 9, 15, 8, 12, 8, 8,
              11, 9, 15, 8, 12, 8, 8, 11, 10, 12, 13, 5, 13, 8, 11, 9, 15, 8,
              12, 8, 8, 11, 10, 15, 13, 5, 13, 8, 11, 9, 15, 8, 12, 8, 8, 11,
              10, 15, 13, 5, 13, 8, 11, 9, 15, 8, 12, 8, 8,
            ],
            markPoint: {
              data: [
                { type: "max", name: "最大值" },
                { type: "min", name: "最小值" },
              ],
            },
            markLine: {
              data: [{ type: "average", name: "平均值" }],
            },
          },
          {
            name: "访问IP",
            type: "line",
            smooth: true,
            data: [
              8, 2, 7, 5, 9, 10, 7, 8, 2, 7, 5, 9, 10, 7, 1, 2, 2, 5, 3, 2, 0,
              8, 2, 7, 5, 9, 10, 7, 8, 2, 7, 5, 9, 10, 7, 1, 2, 2, 5, 3, 2, 0,
              8, 2, 7, 5, 9, 10, 7, 1, 2, 2, 5, 3, 2, 0, 8, 2, 7, 5, 9, 10, 7,
              8, 2, 7, 5, 9, 10, 7, 8, 2, 7, 5, 9, 10, 7, 8, 2, 7, 5, 9, 10, 7,
              1, 2, 2, 5, 3, 2, 0, 8, 2, 7, 5, 9, 10, 7, 8, 2, 7, 5, 9, 10, 7,
              8, 2, 7, 5, 9, 10, 7, 1, 2, 2, 5, 3, 2, 0, 8, 2, 7, 5, 9, 10, 7,
              8, 2, 7, 5, 9, 10, 7, 8, 2, 7, 5, 9, 10, 7,
            ],
            markPoint: {
              data: [{ name: "周最低", value: -2, xAxis: 1, yAxis: -1.5 }],
            },
            markLine: {
              data: [{ type: "average", name: "平均值" }],
            },
          },
        ],
      };
      barChart.setOption(lineOption);
    } ; 

onMounted(() => {
  drawBar2() ;
});

onUnmounted(() => {
  echart.dispose;
});


</script>

<style lang="scss" scoped>
.item-health-title{
  margin-bottom: 5px !important;
}
.item-health-count{
  margin-bottom: 5px;
}


.item-box {
  ul {
    list-style: none;
    padding: 0px;
    margin: 0px;

    .server-desc {
      font-size: 1.6rem;
      color: #3b5998 ; 
    }

    .active {
      background: #3b5998 !important;
      color: #fff !important;

      .server-desc {
        color: #fff !important;
      }
    }

    li.item-box-info {
      padding: 8px;
      float: left;
      width: 100%;
      background: #f9fbfd;
      border-radius: 5px;
      color: #222;
      margin-bottom: 5px;

      .item-status {
        float: left;
      }

      .status-info {
        float: left;
        margin-left: 20px;

        .item-text {
          margin-bottom: 5px;
          font-size: 12px;
          font-weight: 600;
          line-height: 1.67;
        }

        .item-num {
          height: 22px;
          line-height: 22px;
          font-size: 18px;
          font-weight: 600;

          .total-num {
            font-size: 14px;
          }
        }
      }
    }
  }
}

.sidecard-bar {
  padding: 10px;
}

.item-list {
  .item-icon {
    img {
      width: 94px;
      height: 20px;
    }
  }
}

.box-header {
  .title {
    position: relative;
    height: 20px;
    margin-bottom: 20px;
    font-size: 14px;
    font-weight: 600;
    line-height: 1.43;
    zoom: 1;
  }

  .box-body {
    // padding: 20px;
    float: left;
    width: 100%;
    border-radius: 5px;
    background: #f9fbfd;

    ul {
      list-style: none;
      padding: 0px;
      margin: 0px;

      li.item-list {
        float: left;
        width: calc(33% - 10px);
        margin-right: 10px;
        background: #f9fbfd;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 10px;

        .item-icon {
          float: left;
        }

        .item-label {
          float: right;
        }
      }
    }

    .sidecard-pie {
      float: left;
      margin-left: 30px;
    }

    .sidecard {
      float: left;
      display: flex;
      flex-direction: column;
      justify-content: center;
      margin-left: 45px;
      margin-top: 40px;

      .box-title {
        font-size: 16px;
        font-weight: 600;
        line-height: 1.43;
        margin-bottom: 5px;
      }
    }
  }
}

</style>