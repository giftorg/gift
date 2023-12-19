<script setup>
import * as anData from '@/assets/json/lottie.json'
import { onMounted } from 'vue'
import lottie from 'lottie-web'

let animation = {}

onMounted(() => {
  animation = lottie.loadAnimation({
    container: document.getElementById('cat'),
    renderer: 'svg',
    loop: true,
    autoplay: true,
    animationData: anData.default
  })

  animation.play()
})
</script>

<template>
  <div id="cat"></div>
</template>

<style scoped>
#cat {
  width: 200px;
  margin-bottom: -5px;
  margin-right: 5px;
}
</style>
