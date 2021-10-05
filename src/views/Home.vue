<template>
  <div >
    <el-container>
      <el-header>
        <div style="display: flex">
          <div style="width: 300px">
            <span>输入</span>
            <el-input-number v-model="form.num" :min="1" :max="200" style="width: 180px;margin-left: 20px"  @change="handleChange" />
          </div>
          <el-input
                  v-model="form.input"
                  :rows="2"
                  type="textarea"
                  placeholder="Please input"
                  style="flex: 1;line-height: 60px"
          />
          <el-button @click="submit" style="height: 50px;margin: 0 20px;">提交</el-button>
          <span style="width: 300px;font-weight: lighter;font-size: 14px;">输入要求: 空格分隔单词，回车结束一次输入</span>

        </div>
      </el-header>
      <el-main style="height: 60vh">
        <el-input
                v-model="res"
                autosize
                type="textarea"
                placeholder="loading..."
                style="flex: 1;line-height: 60px"
        />
      </el-main>
      <el-footer>
        <el-collapse v-model="activeNames" @change="handleChange">
          <el-collapse-item title="点我查看说明" name="3">
            <div>
              1. 如果提交后很长时间没有反应，则说明您的输入不符合规定，查看有没有多余的空行之类的原因
            </div>
            <div>
              2. 关于界面展示，可能会很难以接受，特别是表格输出，歪歪扭扭的，不过这也没办法，只会设计成这样，体谅体谅
            </div>
            <div>
              3. 如果你发现计算结果和你的有偏差，别犹豫，联系我，因为这些逻辑全是自己写的，难免会有失误，要是你看了我的源码就知道这逻辑写的又臭又长<br/>
              总之，先别怀疑自己，怀疑怀疑我
            </div>
            <div>
              4. 最后，补一点输入格式，注意每个字符之间以空格结束,关于左递归的判断，很容易后台判断错误就死了，所以尽量不要写左递归文法(直接|间接)<br/>
              补一个案例在这以供参考：<br/>
              E -> E + T<br/>
              E -> T<br/>
              T -> T * F<br/>
              T -> F<br/>
              F -> P ^ F<br/>
              F -> P<br/>
              P -> ( E )<br/>
              P -> i<br/>
              P -> #<br/>
              其中#代表空集，返回的结果中 ~代表末尾标记
            </div>
            <div>
              5. 需要源码的可以去本人博客(http://106.14.196.77)获取,资源位于更多->杂项中
            </div>
          </el-collapse-item>

        </el-collapse>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
// @ is an alias to /src

// import request from "../uitls/request";
import axios from 'axios'
export default {

  name: 'Home',
  data(){
    return{
      form: {
        num: 1,
        input: ""
      },
      res:""
    }
  }
  ,
  methods:{
    submit(){
      axios.post('/api/handle',this.form).then(res=>{
        console.log(res)
        this.res=res.data
      })
    }
  }
}
</script>

<style>

  .el-header{
    background-color: #b3c0d1;
    color: var(--el-text-color-primary);
    /*text-align: center;*/
    line-height: 60px;
    height: auto;
  }
  .el-main {
    background-color: #e9eef3;
    color: var(--el-text-color-primary);
    text-align: center;
  }
</style>