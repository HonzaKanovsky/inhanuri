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
const pageSize = 10; // Number of items per page

// Search & Filter
const searchType = ref<"title" | "tag">("title"); // Default: Search by Title
const searchQuery = ref(""); // User input for title search
const selectedTag = ref(""); // Selected tag for filtering


const sortKey = ref<string | null>(null);
const sortOrder = ref<"asc" | "desc">("desc");

// Fetch notices with sorting
const fetchCommunities = async (
  page: number = 0,
  filterBy: string = "",
  query: string = "",
  sort: string | null = null,
  order: "asc" | "desc" = "asc"
) => {
  try {
    const response = await axios.get<Page<Community>>(`community/filtered`, {
      params: {
        page,
        size: pageSize,
        filterBy: filterBy || undefined,
        query: query || undefined,
        sort: sort || undefined,
        order: sort ? order : undefined, // Send order only if sorting
      },
    });

    notices.value = response.data.content.map((item: any) => new Community(item));
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error("Error fetching notices:", error);
  }
};

// Handle sorting
const applySort = (column: string) => {
  if (sortKey.value === column) {
    // Toggle order if clicking the same column
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    // Set new column and default to ascending order
    sortKey.value = column;
    sortOrder.value = "asc";
  }
  fetchCommunities(0, searchType.value === "title" ? "title" : "tag", searchType.value === "title" ? searchQuery.value : selectedTag.value, sortKey.value, sortOrder.value);
};

const goToDetail = (id: number) => {
  router.push(`/community/${id}`);
};

// Handle search/filter request
const applyFilter = () => {
  if (searchType.value === "title") {
    fetchCommunities(0, "title", searchQuery.value);
  } else {
    fetchCommunities(0, "tag", selectedTag.value);
  }
};

// Handle pagination navigation
const goToPage = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    fetchCommunities(page);
  }
};

const goToCreatePost = () => {
  router.push("/community/create");
};

// Fetch data on component mount
onMounted(() => {
  fetchCommunities();
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
        {{ t("community.title") }}
      </h1>

      <div class="mx-auto md:mx-0">
        <!-- Search & Filter -->
        <div class="flex flex-col sm:flex-row gap-4 mt-10 w-[80vw] lg:w-auto  justify-start">
          <!-- Search Type Selection -->
          <select v-model="searchType" class="border p-2 rounded">
            <option value="title">Title</option>
            <option value="tag">Tag</option>
          </select>

          <!-- Input for Title Search -->
          <input v-if="searchType === 'title'" v-model="searchQuery" type="text" placeholder="Enter title..."
            class="border p-2 rounded w-full sm:w-60" />

          <!-- Dropdown for Tag Selection -->
          <select v-if="searchType === 'tag'" v-model="selectedTag" class="border p-2 rounded">
            <option disabled value="">Select a tag</option>
            <option v-for="tag in tags" :key="tag" :value="tag">
              {{ tag }}
            </option>
          </select>

          <!-- Filter Button -->
          <button @click="applyFilter"
            class="px-4 py-2 bg-cal_poly_green-600 text-white rounded hover:bg-cal_poly_green-500">
            Filter
          </button>

          <button @click="goToCreatePost"
            class="px-4 py-2 md:ml-auto mr-0 bg-cal_poly_green-600 text-white rounded hover:bg-cal_poly_green-500">
            + Create Post
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
                {{ t("community.table.title") }}
                <span v-if="sortKey === 'title'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
              <th class="px-6 py-3 whitespace-nowrap hidden sm:table-cell" @click="applySort('tag')">
                {{ t("community.table.tag") }}
                <span v-if="sortKey === 'tag'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
              <th @click="applySort('updatedAt')" class="px-6 py-3 whitespace-nowrap cursor-pointer">
                {{ t("community.table.date") }}
                <span v-if="sortKey === 'updatedAt'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
              <th class="px-6 py-3 whitespace-nowrap hidden sm:table-cell" @click="applySort('comments')">
                {{ t("community.table.comments") }}
                <span v-if="sortKey === 'commentCount'">{{ sortOrder === "asc" ? "▲" : "▼" }}</span>
              </th>
              <th class="px-6 py-3 whitespace-nowrap hidden sm:table-cell" @click="applySort('views')">
                {{ t("community.table.views") }}
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
              <td class="px-6 py-3 text-center hidden sm:table-cell">{{ notice.commentCount }}</td>
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
