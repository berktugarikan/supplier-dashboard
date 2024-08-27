import { createRouter, createWebHistory } from 'vue-router';
import SupplierList from '@/components/SupplierList.vue';
import SupplierDetail from '@/components/SupplierDetail.vue';

const routes = [
  {
    path: '/',
    redirect: '/suppliers'
  },
  {
    path: '/suppliers',
    name: 'SupplierList',
    component: SupplierList
  },
  {
    path: '/supplier/:id',
    name: 'SupplierDetail',
    component: SupplierDetail,
    props: true
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || 'Varsayılan Başlık';
  next();
});


export default router;
