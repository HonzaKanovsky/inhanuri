<template>
    <nav class="bg-white">
        <div class="mx-auto max-w-[90rem] px-2 sm:px-6 lg:px-8">
            <div class="flex h-[10vh] items-center justify-between">
                <div class="flex items-center">
                    <RouterLink class="flex flex-shrink-0 items-center mr-4" to="/">
                        <img src="../assets/img/inhanuri_logo.png" class="max-h-[8vh]">
                        <span class="hidden md:block text-black_olive-200 text-4xl font-bold ml-2"></span>
                    </RouterLink>
                </div>
                
                <!-- Burger Menu Button -->
                <button @click="isMenuOpen = !isMenuOpen" class="md:hidden text-black_olive-400 focus:outline-none">
                    <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6h7"></path>
                    </svg>
                </button>
                
                <!-- Navigation Links -->
                <div class="hidden md:flex md:items-center md:space-x-4 w-full md:w-auto">
                    <div class="flex flex-col md:flex-row w-full md:w-auto text-2xl font-bold items-center">
                        <RouterLink to="/" :class="[isActiveLink('/') ? 'text-forest_green-500' : 'hover:text-black_olive-700', 'text-gray-500', 'px-3', 'py-2', 'rounded-md']">
                            {{ t('navbar.home') }}
                        </RouterLink>
                        <RouterLink to="/notice" :class="[isActiveLink('/notice') ? 'text-forest_green-500' : 'hover:text-black_olive-700', 'text-gray-500', 'px-3', 'py-2', 'rounded-md']">
                            {{ t('navbar.notice') }}
                        </RouterLink>
                        <RouterLink to="/community" :class="[isActiveLink('/community') ? 'text-forest_green-500' : 'hover:text-black_olive-700', 'text-gray-500', 'px-3', 'py-2', 'rounded-md']">
                            {{ t('navbar.community') }}
                        </RouterLink>
                        <RouterLink to="/questions" :class="[isActiveLink('/questions') ? 'text-forest_green-500' : 'hover:text-black_olive-700', 'text-gray-500', 'px-3', 'py-2', 'rounded-md']">
                            {{ t('navbar.questions') }}
                        </RouterLink>
                    </div>
                </div>
            </div>
            
            <!-- Dropdown Menu -->
            <div v-if="isMenuOpen" class="md:hidden absolute top-[10vh] left-0 w-full bg-white shadow-md py-4 z-10">
                <div class="flex flex-col items-center space-y-4 text-2xl font-bold">
                    <RouterLink @click="isMenuOpen = false" to="/" class="text-gray-500 hover:text-black_olive-700 px-3 py-2 rounded-md">
                        {{ t('navbar.home') }}
                    </RouterLink>
                    <RouterLink @click="isMenuOpen = false" to="/notice" class="text-gray-500 hover:text-black_olive-700 px-3 py-2 rounded-md">
                        {{ t('navbar.notice') }}
                    </RouterLink>
                    <RouterLink @click="isMenuOpen = false" to="/community" class="text-gray-500 hover:text-black_olive-700 px-3 py-2 rounded-md">
                        {{ t('navbar.community') }}
                    </RouterLink>
                    <RouterLink @click="isMenuOpen = false" to="/questions" class="text-gray-500 hover:text-black_olive-700 px-3 py-2 rounded-md">
                        {{ t('navbar.questions') }}
                    </RouterLink>
                </div>
            </div>
        </div>
    </nav>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';

const isMenuOpen = ref(false);

const isActiveLink = (routePath: string) => {
    const route = useRoute();
    return route.path === routePath;
};

const { t } = useI18n();
</script>
