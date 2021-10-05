<template>
  <div>
    <div id="nav" style="height: 200px">
      <Header/>
    </div>
    <router-view style="height: 100%"/>
  </div>
</template>

<script>
  import Header from "./components/Header";
  export default {
    name:"App",
    components:{
      Header
    }

  }
</script>