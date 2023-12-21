<script setup>
const props = defineProps({
  project: {
    fullName: {
      type: String,
      default: ''
    },
    description: {
      type: String,
      default: ''
    },
    tags: {
      type: Array,
      default: []
    }
  }
})
</script>

<template>
  <div id="card" v-show="props?.project?.readmeCn?.length > 0">
    <div class="project-full-name">
      <a :href="`https://githubfast.com/${props.project.fullName}`" target="_blank">{{ props.project.fullName }}</a>
    </div>
    <div class="project-desc">
      {{ props.project.description }}
    </div>
    <div class="project-tags">
      <div class="project-tag" v-for="tag in props.project.tags.slice(0, 7)">
        {{ tag }}
      </div>
    </div>
  </div>
</template>

<style scoped lang="less">
#card {
  background: var(--cark-color);
  //color: #2976f6;
  padding: 20px;
  border-radius: 2px;
  border: 1px solid rgb(225, 226, 230);

  display: flex;
  flex-direction: column;
  gap: 24px;

  .project-full-name {
    font-size: 24px;
  }

  .project-desc {
    color: #666;
  }

  .project-tags {
    display: flex;
    flex-direction: row;
    gap: 10px;

    .project-tag {
      background-color: #f2f2f5;
      padding: 3px 4px;
      font-size: 12px;
      border-radius: 5px;
    }
  }
}
</style>