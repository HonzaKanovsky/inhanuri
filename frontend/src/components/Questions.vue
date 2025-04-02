<script lang="ts" setup>
import { useI18n } from "vue-i18n";
import { ref, onMounted } from "vue";
import axios from "axios";
import { Community } from "@/models/Community";
import type { Page } from "@/Interfaces/Page"; // Adjust import path
import { tags } from "@/enums/tags";
import { useRouter } from "vue-router";

const router = useRouter();
const { t } = useI18n();

// Notices data
const notices = ref<Community[]>([]);

// Pagination
const currentPage = ref(0);
const totalPages = ref(1);
const pageSize = 10;

// Search & Filter
const searchType = ref<"title" | "tag">("title");
const searchQuery = ref("");
const selectedTag = ref("");

// Sorting
const sortKey = ref<string | null>(null);
const sortOrder = ref<"asc" | "desc">("desc");

// Fetch notices from API
const fetchQuestions = async (
  page: number = 0,
  filterBy: string = "",
  query: string = "",
  sort: string | null = null,
  order: "asc" | "desc" = "asc"
) => {
  try {
    const response = await axios.get<Page<Community>>(`questions/filtered`, {
      params: {
        page,
        size: pageSize,
        filterBy: filterBy || undefined,
        query: query || undefined,
        sort: sortKey.value, // Sorting field
        order: sortOrder.value, // Sorting order
      },
    });

    notices.value = response.data.content.map((item: any) => new Community(item));
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;

  } catch (error) {
    console.error("Error fetching notices:", error);
  }
};

const applySort = (column: string) => {
  if (sortKey.value === column) {
    // Toggle order if clicking the same column
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    // Set new column and default to ascending order
    sortKey.value = column;
    sortOrder.value = "asc";
  }
  fetchQuestions(0, searchType.value === "title" ? "title" : "tag", searchType.value === "title" ? searchQuery.value : selectedTag.value, sortKey.value, sortOrder.value);
};

const goToDetail = (id: number) => {
  router.push(`/questions/${id}`);
};

// Handle search/filter request
const applyFilter = () => {
  fetchQuestions(0, searchType.value, searchType.value === "title" ? searchQuery.value : selectedTag.value);
};

// Handle pagination
const goToPage = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    fetchQuestions(page);
  }
};

// Sorting logic
const toggleSort = (column: string) => {
  if (sortKey.value === column) {
    // Toggle order if already sorting by this column
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    // Set new column and reset to descending order
    sortKey.value = column;
    sortOrder.value = "desc";
  }
  fetchQuestions(0);
};

// Navigate to create post
const goToCreatePost = () => {
  router.push("/questions/create");
};

// Fetch data on component mount
onMounted(() => {
  fetchQuestions();
});

// Format date for display
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString();
};
</script>

<template>
  <section class="bg-white">
    <div class="max-w-5xl mx-auto flex flex-col pt-10 min-h-[80vh]">
      <h1 class="text-5xl font-bold gradient-background-right bg-clip-text text-transparent mx-auto">
        {{ t("questions.title") }}
      </h1>

      <div class="mx-auto md:mx-0">

        <!-- Search & Filter -->
        <div class="flex flex-col sm:flex-row gap-4 mt-10 justify-start w-[80vw] sm:w-full">
          <select v-model="searchType" class="border p-2 rounded">
            <option value="title">Title</option>
            <option value="tag">Tag</option>
          </select>

          <input v-if="searchType === 'title'" v-model="searchQuery" type="text" placeholder="Enter title..."
            class="border p-2 rounded w-full sm:w-60" />

          <select v-if="searchType === 'tag'" v-model="selectedTag" class="border p-2 rounded">
            <option disabled value="">Select a tag</option>
            <option v-for="tag in tags" :key="tag" :value="tag">
              {{ tag }}
            </option>
          </select>

          <button @click="applyFilter"
            class="px-4 py-2 bg-cal_poly_green-600 text-white rounded hover:bg-cal_poly_green-500">
            Filter
          </button>

          <button @click="goToCreatePost"
            class="px-4 py-2 sm:ml-auto bg-cal_poly_green-600 text-white rounded hover:bg-cal_poly_green-500">
            + Ask question
          </button>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full mt-5">
          <!-- Table Head -->
          <thead class="bg-slate-50 border-y-2 border-t-black text-black">
            <tr class="text-center">
              <th class="px-6 py-3 whitespace-nowrap">No.</th>
              <th @click="applySort('title')" class="px-6 py-3 w-full cursor-pointer">
                {{ t("questions.table.title") }}
                <span v-if="sortKey === 'title'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
              <th @click="applySort('tag')" class="px-6 py-3 whitespace-nowrap cursor-pointer hidden sm:table-cell">
                {{ t("questions.table.tag") }}
                <span v-if="sortKey === 'tag'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
              <th @click="applySort('updatedAt')" class="px-6 py-3 whitespace-nowrap cursor-pointer">
                {{ t("questions.table.date") }}
                <span v-if="sortKey === 'updatedAt'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
              <th @click="applySort('answers')" class="px-6 py-3 whitespace-nowrap cursor-pointer hidden sm:table-cell">
                {{ t("questions.table.answers") }}
                <span v-if="sortKey === 'answers'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
              <th @click="applySort('views')" class="px-6 py-3 whitespace-nowrap cursor-pointer hidden sm:table-cell">
                {{ t("questions.table.views") }}
                <span v-if="sortKey === 'views'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
            </tr>
          </thead>

          <!-- Table Body -->
          <tbody>
            <tr v-for="(notice, index) in notices" :key="notice.id" class="hover:bg-gray-100 hover:cursor-pointer"
              @click="goToDetail(notice.id)">
              <td class="px-6 py-3 text-center">{{ currentPage * pageSize + index + 1 }}</td>
              <td class="px-6 py-3">{{ notice.title }}</td>
              <td class="px-6 py-3 text-center hidden sm:table-cell">{{ notice.tag }}</td>
              <td class="px-6 py-3 text-center">{{ formatDate(notice.updatedAt) }}</td>
              <td class="px-6 py-3 text-center hidden sm:table-cell">{{ notice.answerCount }}</td>
              <td class="px-6 py-3 text-center hidden sm:table-cell">{{ notice.views }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Controls -->
      <div class="flex justify-center mt-5 space-x-2">
        <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 0"
          class="px-4 py-2 bg-gray-200 rounded disabled:opacity-50">
          ⬅ Previous
        </button>

        <span class="px-4 py-2 bg-gray-100 rounded">
          Page {{ currentPage + 1 }} of {{ totalPages }}
        </span>

        <button @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages - 1"
          class="px-4 py-2 bg-gray-200 rounded disabled:opacity-50">
          Next ➡
        </button>
      </div>
    </div>
  </section>
</template>
