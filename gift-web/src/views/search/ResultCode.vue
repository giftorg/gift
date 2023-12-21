<script setup>
import CodeCard from '@/components/pages/result/CodeCard.vue'
import Nav from '@/components/common/Nav.vue'
import Footer from '@/components/common/Footer.vue'
import { useRoute, useRouter } from 'vue-router'
import { onMounted, reactive } from 'vue'
import { getSearchCode } from '@/services/search/index.js'
import SearchInput from '@/components/common/SearchInput.vue'

const route = useRoute()
const router = useRouter()

const codes = reactive([])

function search(query) {
  location.href = `/search/code?query=${query}`
}

onMounted(() => {
  const query = route.query.query
  getSearchCode(query).then(res => {
    codes.length = 0
    for (let c of res.data) {
      codes.push(c)
    }
  })
})
</script>

<template>
  <div class="max-height flex-column" id="result-project">
    <!--导航栏-->
    <Nav></Nav>

    <div class="ui container">
      <SearchInput @enter="search"></SearchInput>
    </div>

    <div class="results ui container">
      <div class="result" v-for="p in codes" :key="p.id">
        <CodeCard :code="p"></CodeCard>
      </div>
    </div>

    <!--底部 footer-->
    <Footer></Footer>
  </div>
</template>

<style scoped lang="less">
#result-project {
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  background: var(--bak-color);
}

.results {
  flex: 1;

  display: flex;
  flex-direction: column;
  gap: 20px;

  margin-top: 20px;
}
</style>
