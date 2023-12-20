<script setup>

</script>

<template>
  <div id="not-found">
    <h1>Oops! 页面未找到</h1>
    <p>抱歉，您请求的页面未找到。</p>
    <p>请检查您输入的网址是否正确！</p>
  </div>
</template>

<style scoped>
#not-found {
  background-color: #222;
  color: #fff;
  font-family: Arial, sans-serif;
  text-align: center;
  padding-top: 100px;

  h1 {
    font-size: 48px;
    margin-bottom: 20px;
  }

  p {
    font-size: 24px;
    margin-bottom: 40px;
  }

  a {
    color: #fff;
    text-decoration: none;
  }

  a:hover {
    text-decoration: underline;
  }
}
</style>