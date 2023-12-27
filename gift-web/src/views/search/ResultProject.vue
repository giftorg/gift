<script setup>
import ProjectCard from '@/components/pages/result/ProjectCard.vue'
import Nav from '@/components/common/Nav.vue'
import Footer from '@/components/common/Footer.vue'
import {useRoute, useRouter} from 'vue-router'
import {onMounted, reactive} from 'vue'
import {getSearchProject} from '@/services/search/index.js'
import SearchInput from '@/components/common/SearchInput.vue'

const route = useRoute()
const router = useRouter()

const projects = reactive([])

function search(query) {
  location.href = `/search/project?query=${query}`
}

onMounted(() => {
  const query = route.query.query
  getSearchProject(query).then(res => {
    projects.length = 0
    const pset = new Set()
    for (let p of res.data) {
      const repoId = p['repoId']
      if (!pset.has(repoId)) {
        projects.push(p)
        pset.add(repoId)
      }
    }
    console.log(pset)
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
      <div class="result" v-for="p in projects" :key="p.id" v-show="p?.readmeCn?.length">
        <ProjectCard :project="p"></ProjectCard>
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
