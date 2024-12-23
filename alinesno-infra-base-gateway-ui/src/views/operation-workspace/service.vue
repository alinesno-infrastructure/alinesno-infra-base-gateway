<template>
  <div>
    <el-row class="acp-dashboard-panel" :gutter="20">

      <el-col :span="19">

        <div class="grid-content">
          <div class="panel-header">
            <div class="header-title"><i class="fa-solid fa-fire"></i> 接口调用热力图</div>
          </div>
          <div class="panel-body acp-height-auto">
        <div class="box-header">
          <el-row>
            <el-col :span="24">
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
  {id:'1' , title:'异常告警信息' , count:45} ,
  {id:'2' , title:'正常运行信息' , count:145} ,
  {id:'3' , title:'告警发送成功' , count:65} ,
  {id:'4' , title:'告警发送失败' , count:85} ,
  {id:'5' , title:'事件提醒次数' , count:45} ,
])

function drawBar2() {
      let barChart = echart.init(
        document.getElementById("echarts-bar2-chart")
      );

      var lineOption;

      // prettier-ignore
      const hours = [
          '12a', '1a', '2a', '3a', '4a', '5a', '6a',
          '7a', '8a', '9a', '10a', '11a',
          '12p', '1p', '2p', '3p', '4p', '5p',
          '6p', '7p', '8p', '9p', '10p', '11p'
      ];
      // prettier-ignore
      const days = ['星期六', '星期五', '星期四','星期三', '星期二', '星期一', '星期日'];

      // prettier-ignore
      const data = [[0, 0, 5], [0, 1, 1], [0, 2, 0], [0, 3, 0], [0, 4, 0], [0, 5, 0], 
                    [0, 6, 0], [0, 7, 0], [0, 8, 0], [0, 9, 0], [0, 10, 0], [0, 11, 2], 
                    [2, 1, 1], [2, 2, 0], [2, 3, 0], [2, 4, 0], [2, 5, 0], [2, 6, 0], [2, 7, 0], 
                    [2, 22, 2], [2, 23, 4], [3, 0, 7], [3, 1, 3], [3, 2, 0], [3, 3, 0], [3, 4, 0], 
                    [3, 5, 0], [3, 6, 0], [3, 7, 0], [3, 8, 1], [3, 9, 0], [3, 10, 5], [3, 11, 4], 
                    [3, 12, 7], [3, 13, 14], [3, 14, 13], [3, 15, 12], [3, 16, 9], [3, 17, 5], [3, 18, 5], 
                    [3, 19, 10], [3, 20, 6], [3, 21, 4], [3, 22, 4], [3, 23, 1], [4, 0, 1], [4, 1, 3], 
                    [0, 12, 4], [0, 13, 1], [0, 14, 1], [0, 15, 3], [0, 16, 4], [0, 17, 6], 
                    [0, 18, 4], [0, 19, 4], [0, 20, 3], [0, 21, 3], [0, 22, 2], [0, 23, 5], 
                    [2, 8, 0], [2, 9, 0], [2, 10, 3], [2, 11, 2], [2, 12, 1], [2, 13, 9], [2, 14, 8], 
                    [2, 15, 10], [2, 16, 6], [2, 17, 5], [2, 18, 5], [2, 19, 5], [2, 20, 7], [2, 21, 4], 
                    [1, 0, 7], [1, 1, 0], [1, 2, 0], [1, 3, 0], [1, 4, 0], [1, 5, 0], [1, 6, 0], 
                    [1, 7, 0], [1, 8, 0], [1, 9, 0], [1, 10, 5], [1, 11, 2], [1, 12, 2], 
                    [1, 13, 6], [1, 14, 9], [1, 15, 11], [1, 16, 6], [1, 17, 7], [1, 18, 8], 
                    [1, 19, 12], [1, 20, 5], [1, 21, 5], [1, 22, 7], [1, 23, 2], [2, 0, 1], 
                    [4, 2, 0], [4, 3, 0], [4, 4, 0], [4, 5, 1], [4, 6, 0], [4, 7, 0], [4, 8, 0], [4, 9, 2], [4, 10, 4], [4, 11, 4], [4, 12, 2], [4, 13, 4], [4, 14, 4], [4, 15, 14], [4, 16, 12], [4, 17, 1], [4, 18, 8], [4, 19, 5], [4, 20, 3], [4, 21, 7], [4, 22, 3], [4, 23, 0], [5, 0, 2], [5, 1, 1], [5, 2, 0], [5, 3, 3], [5, 4, 0], [5, 5, 0], [5, 6, 0], [5, 7, 0], [5, 8, 2], [5, 9, 0], [5, 10, 4], [5, 11, 1], [5, 12, 5], [5, 13, 10], [5, 14, 5], [5, 15, 7], [5, 16, 11], [5, 17, 6], [5, 18, 0], [5, 19, 5], [5, 20, 3], [5, 21, 4], [5, 22, 2], [5, 23, 0], [6, 0, 1], [6, 1, 0], [6, 2, 0], [6, 3, 0], [6, 4, 0], [6, 5, 0], [6, 6, 0], [6, 7, 0], [6, 8, 0], [6, 9, 0], [6, 10, 1], [6, 11, 0], [6, 12, 2], [6, 13, 1], [6, 14, 3], [6, 15, 4], [6, 16, 0], [6, 17, 0], [6, 18, 0], [6, 19, 0], [6, 20, 1], [6, 21, 2], [6, 22, 2], [6, 23, 6]]
          .map(function (item) {
          return [item[1], item[0], item[2] || '-'];
      });

      lineOption= {
        tooltip: {
          position: 'top'
        },
        grid: {
          height: '90%',
          width: '90%',
          top: '1%'
        },
        xAxis: {
          type: 'category',
          data: hours,
          splitArea: {
            show: true
          }
        },
        yAxis: {
          type: 'category',
          data: days,
          splitArea: {
            show: true
          }
        },
        visualMap: {
          min: 0,
          max: 10,
          calculable: true,
          orient: '',
          left: 'left',
          bottom: '0%'
        },
        series: [
          {
            name: '异常次数',
            type: 'heatmap',
            data: data,
            label: {
              show: true
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
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