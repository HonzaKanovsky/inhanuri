import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import NoticeView from '@/views/NoticeView.vue'
import NoticeCreateView from '@/views/NoticeCreateView.vue'
import NoticeEditView from '@/views/NoticeEditView.vue'
import QuestionsView from '@/views/QuestionsView.vue'
import CommunityView from '@/views/CommunityView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import NoticeDetailView from '@/views/NoticeDetailView.vue'
import CommunityCreateView from '@/views/CommunityCreateView.vue'
import CommunityDetailView from '@/views/CommunityDetailView.vue'
import QuestionsDetailView from '@/views/QuestionsDetailView.vue'
import CommunityEditView from '@/views/CommunityEditView.vue'
import QuestionsCreateView from '@/views/QuestionsCreateView.vue'
import QuestionsEditView from '@/views/QuestionsEditView.vue'
import LoginView from '@/views/LoginView.vue'

const publicPages = [
  "/", "/notice", "/community", "/questions", "/404", "/community/create", "/login"
];

const loginPages = ["/login"];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/notice', name: 'notice', component: NoticeView },
    { path: '/notice/:id', name: 'noticeDetail', component: NoticeDetailView },
    { path: '/notice/create', name: 'notice-create', component: NoticeCreateView },
    { path: '/notice/edit/:id', name: 'notice-edit', component: NoticeEditView },
    { path: '/community', name: 'community', component: CommunityView },
    { path: '/community/:id', name: 'community-detail', component: CommunityDetailView },
    { path: '/community/create', name: 'community-create', component: CommunityCreateView },
    { path: '/community/edit/:id', name: 'community-edit', component: CommunityEditView },
    { path: '/questions', name: 'questions', component: QuestionsView },
    { path: '/questions/:id', name: 'question-detail', component: QuestionsDetailView },
    { path: '/questions/create', name: 'questions-create', component: QuestionsCreateView },
    { path: '/questions/edit/:id', name: 'questions-edit', component: QuestionsEditView },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/404', name: 'not-found', component: NotFoundView },
    { path: '/:catchAll(.*)', redirect: '/404' }
  ],
});

router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem("accessToken");
  console.log();

  const isPublicPage = publicPages.some(page => to.path.startsWith(page)) ||
    to.path.startsWith("/notice/") ||
    to.path.startsWith("/community/") ||
    to.path.startsWith("/questions/");

  if (isAuthenticated && loginPages.includes(to.path)) {
    next("/"); // Redirect logged-in users from login page to home
  } else if (!isAuthenticated && !isPublicPage) {
    next("/login"); // Redirect unauthenticated users to login
  } else {
    next(); // Allow access
  }
});

export default router;
