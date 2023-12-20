import { createApp } from 'vue'
import './style.css'
import 'semantic-ui-css/semantic.min.css'
import App from './App.vue'
import router from './router'
import SuiVue from 'semantic-ui-vue'
import 'highlight.js/styles/xcode.min.css'
import hljs from 'highlight.js/lib/core';
import java from 'highlight.js/lib/languages/java.js';
import hljsVuePlugin from "@highlightjs/vue-plugin";
hljs.registerLanguage('java', java);


const app = createApp(App)

app.use(router)
app.use(SuiVue)
app.use(hljsVuePlugin)
app.mount('#app')
