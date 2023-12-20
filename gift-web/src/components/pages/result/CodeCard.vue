<script setup>
import { reactive } from 'vue'

const props = defineProps({
  code: {
    filePath: {
      type: String,
      default: ''
    },
    project: {
      defaultBranch: {
        type: String,
        default: ''
      },
      fullName: {
        type: String,
        default: ''
      }
    },
    begin: {
      line: {
        type: Number,
        default: 0
      }
    },
    source: {
      type: String,
      default: ''
    },
    description: {
      type: String,
      default: ''
    },
    technologyStack: {
      type: String,
      default: ''
    }
  }
})

const code = reactive(props.code)

code.rpath = code.filePath.replace('/gift/repositories/', '').replace(code.project.fullName, '')
code.path = 'https://github.com/' + code.project.fullName + '/tree/' + code.project.defaultBranch + code.rpath + '#L' + code.begin.line

</script>

<template>
  <div id="card">
    <div class="card-header">
      <a :href="code.path" class="card-header-title" target="_blank">{{ code.project.fullName }}</a>
      <a :href="code.path" class="card-header-path" target="_blank">{{ code.rpath.split('/').slice(-2).join('/') }}</a>
    </div>
    <div class="card-code">
      <highlightjs language='java' :code="code.source.split('\n').slice(0, 10).join('\n')" :style="{'margin': '0'}" />
    </div>
    <div class="card-footer">
      <div class="card-desc">{{code.description}}</div>
      <div class="card-techs">
        <div class="card-tech" v-for="tech in code.technologyStack.split(' ')" :key="tech">{{tech}}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#card {
  display: flex;
  flex-direction: column;
  border-radius: 6px;

  border: 1px solid #cad1d9;

  .card-header {
    background-color: #f5f7f9;
    display: flex;
    gap: 5px;
    padding: 10px;
    border-radius: 6px 6px 0 0;

    .card-header-title {
      font-weight: 600;
      color: #000;
    }

    .card-header-path {
      color: #000;
    }
  }

  .card-footer {
    display: flex;
    flex-direction: column;
    background-color: #f5f7f9;
    gap: 10px;

    border-radius: 0 0 6px 6px;
    padding: 10px;

    .card-desc {
      padding: 5px 5px 0 5px;
    }

    .card-techs {
      display: flex;
      gap: 5px;

      .card-tech {
        background-color: #e5f1ff;
        color: #1653ff;
        padding: 2px 3px;
        font-size: 12px;
        border-radius: 5px;
      }
    }
  }
}
</style>