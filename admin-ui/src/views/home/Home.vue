<template>
  <div>
    <!-- 待办事项 -->
      <el-row :gutter="20">
          <el-col :span="12">
              <div class="section-title">待办事项</div>
              <el-card>
                  <div class="todo-item">
                      <el-checkbox class="todo-checkbox"></el-checkbox>
                      <div class="todo-content">完成月度报表</div>
                      <div class="todo-time">今天 14:00</div>
                  </div>
                  <div class="todo-item">
                      <el-checkbox class="todo-checkbox"></el-checkbox>
                      <div class="todo-content">审核用户注册申请</div>
                      <div class="todo-time">今天 16:00</div>
                  </div>
                  <div class="todo-item">
                      <el-checkbox class="todo-checkbox"></el-checkbox>
                      <div class="todo-content">更新产品信息</div>
                      <div class="todo-time">明天 10:00</div>
                  </div>
                  <div class="todo-item">
                      <el-checkbox class="todo-checkbox"></el-checkbox>
                      <div class="todo-content">准备会议材料</div>
                      <div class="todo-time">明天 14:00</div>
                  </div>
              </el-card>
          </el-col>
          <el-col :span="12">
              <div class="section-title">系统公告</div>
              <el-card>
                  <el-timeline>
                      <el-timeline-item timestamp="2023-10-15" placement="top">
                          <el-card>
                              <h4>系统维护通知</h4>
                              <p>系统将于本周六凌晨2:00-4:00进行维护，期间可能无法访问。</p>
                          </el-card>
                      </el-timeline-item>
                      <el-timeline-item timestamp="2023-10-10" placement="top">
                          <el-card>
                              <h4>新功能上线</h4>
                              <p>数据统计模块新增用户行为分析功能，欢迎使用。</p>
                          </el-card>
                      </el-timeline-item>
                      <el-timeline-item timestamp="2023-10-05" placement="top">
                          <el-card>
                              <h4>版本更新</h4>
                              <p>系统已升级至v2.1.0版本，修复了已知问题，优化了性能。</p>
                          </el-card>
                      </el-timeline-item>
                  </el-timeline>
              </el-card>
          </el-col>
      </el-row>
  </div>
</template>

<script>
import '@/assets/js/china'
export default {

  data() {
    
  },
  methods: {
  },
  watch: {
  }
}
</script>

<style scoped>
.card-icon-container {
  display: inline-block;
  font-size: 3rem;
}
.area-wrapper {
  display: flex;
  justify-content: center;
}
.card-desc {
  font-weight: bold;
  float: right;
}
.card-title {
  margin-top: 0.3rem;
  line-height: 18px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 1rem;
}
.card-count {
  margin-top: 0.75rem;
  color: #666;
  font-size: 1.25rem;
}
.echarts {
  width: 100%;
  height: 100%;
}
.e-title {
  font-size: 13px;
  color: #202a34;
  font-weight: 700;
}
</style>
