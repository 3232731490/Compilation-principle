import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import First from "../components/First";
import VT from "../components/VT";

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    children:[{
        path:'first',
      name:'First',
      component:First
      },
      {
        path:'vt',
        name:'VT',
        component: VT
      }
    ]
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
