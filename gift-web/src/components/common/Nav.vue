<script setup>

import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div id="nav" class="ui inverted segment m-padded-tb-mini padding-LR-zero">
    <div class="ui container">
      <div class="ui inverted secondary menu stackable">
        <h2 class="ui header item" style="padding: 0 !important">
          <img style="width: 120px !important; margin-right: 10px" src="@/assets/images/title.png" alt="" @click="router.push('/')"/>
        </h2>
        <div class="right m-item item m-mobile-hide" style="z-index: 1;">
          <a href="/front/about" :class="['mini', 'm-item', 'about']">关于我们</a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#nav {
  background: var(--bak-color);

  box-shadow: rgba(0, 0, 0, 0.1) 0px 1px 2px 0px;

  .about {
    color: black;
  }
}
</style>