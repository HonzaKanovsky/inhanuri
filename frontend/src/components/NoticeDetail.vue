<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

import axios from "axios";
import { CommunityEditRequest } from "@/models/CommunityEditRequest";

const route = useRoute();
const router = useRouter();
const notice = ref(null);
const files = ref([]);

const isLoggedIn = ref(false);


const { t } = useI18n();

// Fetch notice details
const fetchNoticeDetail = async () => {
  isLoggedIn.value = !!localStorage.getItem("accessToken");

  try {
    const response = await axios.get(`/notice/${route.params.id}`, {
      params: { isFirstVisit: checkFirstVisit() },
    });
    notice.value = response.data;
    files.value = response.data.files || []; // Fetch associated files

  } catch (error) {
    console.error("Error fetching notice:", error);
  }
};

// Format date
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString();
};

// ✅ Back button function
const goBack = () => {
  router.push("/notice"); // Navigates back to the Notices list
};

const handleEditClick = () => {
  // No password needed, navigate directly
  const editRequest = new CommunityEditRequest({
    id: notice.value.id,
    title: notice.value.title,
    tag: notice.value.tag,
    updatedAt: notice.value.updatedAt,
    views: notice.value.views,
    files: files.value,
    token: localStorage.getItem("accessToken"), // Use access token
  });

  router.push({
    path: `/notice/edit/${route.params.id}`,
    state: { notice: editRequest },
  });
};

// Download file
const downloadFile = async (fileID, fileName) => {
  try {
    const response = await axios.get(`/upload/files/${fileID}`, {
      responseType: "blob", // Ensure binary response
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (error) {
    console.error("Error downloading file:", error);
    alert("Failed to download file.");
  }

};

const checkFirstVisit = () => {
  const path = route.path;

  const visitedPaths = JSON.parse(localStorage.getItem("visitedPaths")) || [];

  const isFirstVisit = !visitedPaths.includes(path);

  if (isFirstVisit) {
    visitedPaths.push(path);
    localStorage.setItem("visitedPaths", JSON.stringify(visitedPaths));
  }

  console.log(isFirstVisit)
  console.log(visitedPaths)
  return isFirstVisit;
}

// Fetch data on mount
onMounted(() =>
  fetchNoticeDetail()
);
</script>

<template>
  <section class="bg-slate-50">

    <div class="max-w-5xl mx-auto flex flex-col pt-20 min-h-[80vh]">
      <h1 class="text-5xl font-bold gradient-background-right bg-clip-text text-transparent mx-auto">
        {{ t("notice.title") }}
      </h1>

      <div v-if="notice">

        <div class="flex flex-row items-center mt-5">
          <span class="flex my-4 text-gray-500 text-sm">
            <button @click="goBack" class=" px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-800">
              ← Back to Notices
            </button>
          </span>
          <span class="flex my-4 text-gray-500 text-sm ml-auto">
            <button v-if="isLoggedIn" @click="handleEditClick"
              class="px-4 py-2 bg-sgbus_green-200 text-white rounded hover:bg-gray-800">
              Edit
            </button>
          </span>
        </div>

        <span class="flex flex-row">
          <span class="flex text-gray-500 text-base font-semibold ml-auto">Views:&nbsp; </span>
          <span class="flex text-gray-500 text-base">{{ notice.views }} |&nbsp;</span>
          <span class="flex text-gray-500 text-base font-semibold">Created:&nbsp; </span>
          <span class="flex text-gray-500 text-base"> {{ formatDate(notice.createdAt) }} |&nbsp;</span>
          <span class="flex text-gray-500 text-base font-semibold">Updated:&nbsp; </span>
          <span class="flex text-gray-500 text-base mr-0"> {{ formatDate(notice.updatedAt) }}</span>
        </span>


        <div class="bg-black_olive-700 shadow-lg rounded-t-lg p-6">
          <h1 class="text-3xl font-bold text-center text-white">{{ notice.title }}</h1>
        </div>
        <div class="bg-white shadow-lg rounded-b-lg p-6">
          <div v-html="notice.content" class="prose max-w-[95%] mx-auto"></div>
        </div>

        <!-- File Attachments Table -->
        <div v-if="files.length" class="mt-6">
          <h2 class="text-lg font-semibold">Attachments</h2>
          <table class="table-auto w-full mt-2 border">
            <thead>
              <tr class="bg-gray-200">
                <th class="border px-4 py-2">File Name</th>
                <th class="border px-4 py-2">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="file in files" :key="file.fileId">
                <td class="border px-4 py-2">{{ file.fileName }}</td>
                <td class="border px-4 py-2 text-center">
                  <button @click="downloadFile(file.fileId, file.fileName)" class="text-blue-500 hover:text-blue-700">
                    ⬇ Download
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

      </div>

      <div v-else class="text-center text-gray-500">Loading...</div>
    </div>

  </section>
</template>
