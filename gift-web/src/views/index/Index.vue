<script setup>
import Nav from '@/components/common/Nav.vue'
import Footer from '@/components/common/Footer.vue'
import SearchInput from '@/components/common/SearchInput.vue'
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()

const options = [
  {
    title: 'Github 项目',
    path: 'project'
  },
  {
    title: '源码检索',
    path: 'code'
  }
]

const selected = ref('project')

function search(query) {
  router.push(`/search/${selected.value}?query=${query}`)
}
</script>

<template>
  <div class="max-height flex-column" id="index">
    <!--导航栏-->
    <Nav></Nav>

    <div class="ui container index-container">
      <SearchInput @enter="search"></SearchInput>
      <div class="selector">
        <div v-for="opt in options" :key="opt.path"
             :class="['option', {'option-selected': selected === opt.path}]"
             @click="selected=opt.path">
          <div>{{ opt.title }}</div>
        </div>
      </div>
    </div>

    <!--底部 footer-->
    <Footer></Footer>
  </div>
</template>

<style scoped lang="less">

#index {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  //background: var(--bak-color);
  background: url("@/assets/images/background_7.png");
  background-size: 100%;
  background-position: center;

  .index-container {
    flex: 1;
    padding-top: 8vh;
  }

  .selector {
    display: flex;
    gap: 5px;
    margin-top: 10px;

    .option {
      height: 30px;
      display: flex;
      align-items: center;
      border-radius: 15px;
      background-color: #edf2f8;
      padding: 10px;
      transition: .3s background, color;
    }

    .option:hover {
      background-color: #dfdfdf;
    }

    .option-selected {
      background: linear-gradient(317deg, #8cceb4, #277af3);
      color: #fff;
    }
  }
}
</style>