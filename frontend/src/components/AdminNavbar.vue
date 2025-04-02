<script lang="ts" setup>
import { RouterLink } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ref, onMounted } from 'vue'

const availableLocales = ['en', 'ko', 'ru', 'fr']
const { locale, t } = useI18n();

const switchLocale = (newLocale: string) => {
    if (availableLocales.includes(newLocale)) {
        locale.value = newLocale as typeof locale.value;
        localStorage.setItem('locale', newLocale);
    }
};

const isLoggedIn = ref(false);
const username = ref("");

onMounted(() => {
    isLoggedIn.value = !!localStorage.getItem("accessToken");
    const storedUsername = localStorage.getItem("username");
    if (storedUsername) {
        username.value = storedUsername;
    } else {
        username.value = '';
    }
});

const logoutUser = () => {
    localStorage.removeItem("username");
    localStorage.removeItem("accessToken");
    isLoggedIn.value = false;
    window.location.reload(); // Refresh to reflect logout
};
</script>

<template>
    <nav class="bg-slate-50">
        <div class="mx-auto max-w-[90rem] px-2 sm:px-6 lg:px-8">
            <div class="flex h-[4vh] items-center justify-between">
                <!-- Language Selector -->
                <select @change="switchLocale(($event.target as HTMLSelectElement).value)" :value="locale"
                    class="border rounded px-2 py-1 text-sm">
                    <option v-for="lang in availableLocales" :key="lang" :value="lang">
                        {{ lang.toUpperCase() }}
                    </option>
                </select>

                <div class="flex flex-1 items-center justify-center md:items-stretch md:justify-end space-x-10">


                    <!-- Login/Logout Button -->
                    <div class="flex">
                        <RouterLink v-if="!isLoggedIn" to="/login" class="hover:underline">
                            Administrator Login
                        </RouterLink>
                        <span v-else>
                            <span class="mr-3">Logged in as {{ username }}</span>
                            <button @click="logoutUser" class="hover:underline text-red-500">
                                Logout
                            </button>
                        </span>

                    </div>
                </div>
            </div>
        </div>
    </nav>
</template>
