<script setup>
import Cat from '@/components/common/Cat.vue'
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const emit = defineEmits(['enter'])

const query = ref(route?.query?.query ? route.query.query : '')
const focus = ref(false)

function enter(event) {
  if (event.key === 'Enter') {
    emit('enter', query.value)
  }
}
</script>

<template>
  <div id="search">
    <div class="cat">
      <Cat></Cat>
    </div>
    <div :class="['search-box', {'search-box-focus': focus}]" @focusin="focus=true; console.log('hello')"
         @focusout="focus=false" tabindex="0">
      <img src="@/assets/icon/search.svg" alt="" class="search-icon" v-if="!focus" />
      <img src="@/assets/icon/search_focus.svg" alt="" class="search-icon" v-else />
      <input class="search-input" type="text" v-model="query" @keyup="enter" placeholder="在 Gift 中搜索想要的内容">
    </div>
  </div>
</template>

<style scoped>
#search {
  display: flex;
  flex-direction: column;

  align-items: end;

  .cat {
    padding-right: 5px;
  }

  .search-box {
    width: 100%;
    display: flex;
    align-items: center;

    border: #d9dcdf 2px solid;
    border-radius: 12px;
    padding: 5px;
    transition: .5s border-color;

    .search-icon {
      margin: 10px;
      height: 20px;
    }

    .search-input {
      flex: 1;
      padding: 10px;
      border: none;
      outline: none;
      font-size: 16px;
      background-color: rgba(0, 0, 0, 0);

      :focus {
        border: none;
        background-color: rgba(0, 0, 0, 0);
      }
    }
  }

  .search-box-focus {
    border-color: #006aff;
    animation: search-box-focus-border 3s infinite;
  }
}
</style>

<style>

@keyframes search-box-focus-border {
  0% {
    border-color: #006aff;
  }
  40% {
    border-color: #006aff40;
  }
  70% {
    border-color: #006aff70;
  }
  to {
    border-color: #006aff;
  }
}

</style>