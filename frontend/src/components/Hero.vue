<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from "vue";
import axios from "axios";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

const router = useRouter();
const { t } = useI18n();

// Define state for data
const notices = ref<{ id: number; title: string }[]>([]);
const communities = ref<{ id: number; title: string }[]>([]);
const questions = ref<{ id: number; title: string }[]>([]);

// Detect orientation state
const isPortrait = ref(window.matchMedia("(orientation: portrait)").matches);

const updateOrientation = () => {
    isPortrait.value = window.matchMedia("(orientation: portrait)").matches;
};

onMounted(() => {
    window.addEventListener("resize", updateOrientation);
    updateOrientation();
});

onUnmounted(() => {
    window.removeEventListener("resize", updateOrientation);
});

// Fetch data from /featured endpoint
const fetchFeaturedData = async () => {
    try {
        const response = await axios.get(`/featured`);
        notices.value = response.data.notices || [];
        communities.value = response.data.communities || [];
        questions.value = response.data.questions || [];
    } catch (error) {
        console.error("Error fetching featured data:", error);
    }
};

const truncateTitle = (title: string, maxLength: number = 35) => {
    return title.length > maxLength ? title.substring(0, maxLength) + "..." : title;
};

const goToCreateObject = (path: string) => {
    router.push(path);
};

// Fetch data on component mount
onMounted(fetchFeaturedData);

</script>

<template>
    <section class="bg-slate-50">
        <div class="lg:max-w-[90rem] mx-auto sm:px-6 lg:px-8 flex flex-col lg:flex-row lg:h-[90vh] lg:pb-10">
            <div
                class="lg:w-3/5 flex flex-col mb-4 items-center text-center bg-hero bg-cover bg-center bg-no-repeat h-[50vh] lg:h-[100%] text-white lg:text-black_olive-400">
                <h2 class="mt-[3vh] text-3xl pb-0 mb-0 font-bold">{{ t('homePage.subtitle-one') }}</h2>
                <h2 class="text-3xl mt-0 pt-0 font-bold">{{ t('homePage.subtitle-two') }}</h2>
                <h1 class="text-5xl md:text-7xl font-bold">{{ t('homePage.title') }}</h1>
            </div>

            <div class="flex flex-col space-y-4 lg:space-y-0 w-full lg:w-2/5 rounded-lg text-slate-50">


                <!-- Notices Section -->
                <div class="flex flex-col sm:flex-row bg-forest_green-800 p-6 mx-2 lg:mx-0 h-[25%] text-black">
                    <div class="flex flex-col">
                        <h3 class="text-xl sm:text-2xl font-bold">{{ t('homePage.notice') }}</h3>
                        <ul class="list-disc ml-6 mt-2">
                            <li v-for="notice in notices" :key="notice.id">
                                <RouterLink :to="`/notice/${notice.id}`" class="hover:underline">
                                    {{ truncateTitle(notice.title) }}
                                </RouterLink>
                            </li>
                        </ul>
                    </div>
                    <img class="hidden md:block max-h-40 ml-auto" src="../assets/img/notes.png"
                        alt="Notices">
                </div>

                <!-- Community Section -->
                <div class="flex flex-col sm:flex-row bg-forest_green-700 p-6 mx-2 lg:mx-0 h-[25%] text-black">
                    <div class="flex flex-col">
                        <h3 class="text-xl sm:text-2xl font-bold">{{ t('homePage.community') }}</h3>
                        <ul class="list-disc ml-6 mt-2">
                            <li v-for="community in communities" :key="community.id">
                                <RouterLink :to="`/community/${community.id}`" class="hover:underline">
                                    {{ truncateTitle(community.title) }}
                                </RouterLink>
                            </li>
                        </ul>
                    </div>
                    <img class="hidden md:block max-h-40 ml-auto" src="../assets/img/group.png"
                        alt="Community">
                </div>

                <!-- Q&A Section -->
                <div class="flex flex-col sm:flex-row bg-forest_green-600 p-6 mx-2 lg:mx-0 h-[25%] text-black">
                    <div class="flex flex-col">
                        <h3 class="text-xl sm:text-2xl font-bold">{{ t('homePage.questions') }}</h3>
                        <ul class="list-disc ml-6 mt-2">
                            <li v-for="question in questions" :key="question.id">
                                <RouterLink :to="`/questions/${question.id}`" class="hover:underline">
                                    {{ truncateTitle(question.title) }}
                                </RouterLink>
                            </li>
                        </ul>
                    </div>
                    <img class="hidden md:block max-h-40 ml-auto" src="../assets/img/customer-service.png"
                        alt="Q&A">
                </div>

                <!-- Buttons for Community & Q&A -->
                <div class="flex flex-row gap-2 lg:gap-0 mx-2 lg:mx-0 h-[25%]">
                    <div @click="goToCreateObject('/community/create')"
                        class="flex flex-col items-center justify-center text-center p-6 text-xl sm:text-2xl hover:cursor-pointer bg-forest_green-500 w-full">
                        <div>{{ t('homePage.newPost') }}</div>
                        <div class="text-4xl sm:text-5xl">+</div>
                    </div>
                    <div @click="goToCreateObject('/questions/create')"
                        class="flex flex-col items-center justify-center text-center p-6 text-xl sm:text-2xl hover:cursor-pointer bg-forest_green-400 w-full">
                        <div>{{ t('homePage.newQuestion') }}</div>
                        <div class="text-4xl sm:text-5xl">+</div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</template>
