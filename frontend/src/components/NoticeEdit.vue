<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { QuillEditor } from "@vueup/vue-quill";
import axios from "axios";
import { tags } from "@/enums/tags";
import { CommunityEditRequest } from "@/models/CommunityEditRequest";
import Quill from "quill";
import ImageUploader from 'quill-image-uploader';
import ImageResize from "quill-image-resize";

const router = useRouter();
const route = useRoute();

const notice = ref(null);
const title = ref("");
const content = ref("");
const tag = ref("");
const password = ref("");
const loading = ref(false);
const uploadedFiles = ref([]);
const token = ref(localStorage.getItem("editToken"));
const accessToken = ref(localStorage.getItem("accessToken"));
const isLoggedIn = ref(false);
const showDeleteModal = ref(false);
const deleteConfirmation = ref("");
const deleteLoading = ref(false);

Quill.register('modules/imageUploader', ImageUploader);
Quill.register('modules/imageResize', ImageResize);

const quillOptions = {
  handlers: {
    file: () => {
      const input = document.createElement('input');
      input.setAttribute('type', 'file');
      input.setAttribute('multiple', true); // Allow multiple files
      input.click();
    },
  },
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote', 'code-block'],
      [{ 'header': 1 }, { 'header': 2 }],
      [{ 'list': 'ordered' }, { 'list': 'bullet' }],
      [{ 'script': 'sub' }, { 'script': 'super' }],
      [{ 'indent': '-1' }, { 'indent': '+1' }],
      [{ 'direction': 'rtl' }],
      [{ 'size': ['small', false, 'large', 'huge'] }],
      [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
      [{ 'color': [] }, { 'background': [] }],
      [{ 'font': [] }],
      [{ 'align': [] }],
      ['link', 'image', 'video'],
      ['clean'],
    ],
    imageUploader: {
      upload: async (file) => {
        const formData = new FormData();
        formData.append('image', file);
        try {
          const response = await axios.post('/upload/image', formData, { // Corrected URL
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });
          const imageUrl = `http://localhost:8080${response.data.url}`; // Construct full URL
          return imageUrl;
        } catch (error) {
          console.error('Error uploading image:', error);
          throw error;
        }
      },
    },
    imageResize: {},
  },
};

const fetchCommunityPost = async () => {
  isLoggedIn.value = !!localStorage.getItem("accessToken");

  if (route.state?.notice) {
    notice.value = new CommunityEditRequest(route.state.notice);
  } else {
    try {
      const response = await axios.get(`/notice/${route.params.id}`);
      notice.value = new CommunityEditRequest(response.data);
    } catch (error) {
      console.error("Error fetching notice post:", error);
      alert("Failed to load post.");
      router.push("/notice");
    }
  }

  if (notice.value) {
    title.value = notice.value.title;
    content.value = notice.value.content;
    tag.value = notice.value.tag;
    uploadedFiles.value = notice.value.files || []; // Set the files list to existing files from the notice
  }

  console.log(uploadedFiles.value); // This will help you see the files

};

const updatePost = async () => {
  if (!title.value || !content.value || !tag.value) {
    alert("All fields are required.");
    return;
  }

  loading.value = true;
  const updateUrl = `/notice`;
  const headers = isLoggedIn ? { Authorization: `Bearer ${accessToken.value}` } : {};

  try {
    await axios.put(updateUrl, {
      id: notice.value.id,
      title: title.value,
      content: content.value,
      tag: tag.value,
      password: isLoggedIn ? undefined : password.value,
      token: isLoggedIn ? undefined : token.value,
      fileIds: uploadedFiles.value.map(file => file.fileId),
    }, { headers });

    alert("Post updated successfully!");
    router.push(`/notice/${notice.value.id}`);
  } catch (error) {
    console.error("Error updating notice post:", error);
    alert("Failed to update post.");
  } finally {
    loading.value = false;
  }
};

const deleteQuestion = async () => {
  if (deleteConfirmation.value !== "delete") {
    alert("Please type 'delete' to confirm deletion.");
    return;
  }

  deleteLoading.value = true;
  const deleteUrl = `/notice/${route.params.id}`;
  const headers = isLoggedIn ? { Authorization: `Bearer ${accessToken.value}` } : {};

  try {
    await axios.delete(deleteUrl, {
      headers,
      data: {
        id: notice.value.id,
        token: isLoggedIn ? undefined : token.value,
      },
    });

    alert("Post deleted successfully!");
    router.push("/notice");
  } catch (error) {
    console.error("Error deleting community post:", error);
    alert("Failed to delete post.");
  } finally {
    deleteLoading.value = false;
    showDeleteModal.value = false;
  }
}; 

const handleFileUpload = async (event) => {
  const file = event.target.files[0]; // Single file selection
  if (!file) return;

  const formData = new FormData();
  formData.append("file", file);

  try {
    const response = await axios.post("/upload/files", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    uploadedFiles.value.push(response.data); // Add uploaded file to list
    alert("File uploaded successfully!");
  } catch (error) {
    console.error("Error uploading file:", error);
    alert("Error uploading file. Please try again.");
  }
};

// Deletes a file
const deleteFile = async (fileId) => {
  try {
    await axios.delete(`/upload/files/${fileId}`);
    uploadedFiles.value = uploadedFiles.value.filter(file => file.fileId !== fileId);
  } catch (error) {
    console.error("Failed to delete file:", error);
    alert("Error deleting file.");
  }
};

onMounted(fetchCommunityPost);
</script>



<template>
  <section class="max-w-4xl mx-auto p-6 bg-white shadow-md rounded-lg">
    <h1 class="text-3xl font-bold mb-6 text-center">Edit Notice</h1>

    <form @submit.prevent="updatePost">
      <!-- Title -->
      <div class="mb-4">
        <label class="block text-gray-700">Title:</label>
        <input v-model="title" type="text" class="w-full p-2 border rounded" required />
      </div>

      <!-- Tag Selection -->
      <div class="mb-4">
        <label class="block text-gray-700">Tag:</label>
        <select v-model="tag" class="w-full p-2 border rounded" required>
          <option disabled value="">Select a tag</option>
          <option v-for="tagOption in tags" :key="tagOption" :value="tagOption">
            {{ tagOption }}
          </option>
        </select>
      </div>

      <!-- Rich Text Editor for Content -->
      <div class="mb-4">
        <label class="block text-gray-700">Content:</label>
        <QuillEditor v-model:content="content" theme="snow" content-type="html" 
          class="w-full h-60 border rounded bg-white" :options="quillOptions"/>
      </div>
      <!-- File Upload Input -->
      <div class="mb-4">
        <label class="block text-gray-700">Upload File:</label>
        <input type="file" @change="handleFileUpload" class="w-full p-2 border rounded" />
      </div>

      <!-- Uploaded Files Table -->
      <div v-if="uploadedFiles.length > 0" class="mt-4">
        <h2 class="text-lg font-bold">Uploaded Files</h2>
        <table class="table-auto w-full mt-2 border">
          <thead>
            <tr class="bg-gray-200">
              <th class="border px-4 py-2">File Name</th>
              <th class="border px-4 py-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="file in uploadedFiles" :key="file.fileId">
              <td class="border px-4 py-2">{{ file.fileName }}</td>
              <td class="border px-4 py-2 text-center">
                <button @click="deleteFile(file.fileId)" class="text-red-500 hover:text-red-700">❌</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Submit Button -->
      <button type="submit" class="w-full bg-forest_green-600 text-white py-2 rounded hover:bg-blue-600 disabled:opacity-50"
        :disabled="loading">
        {{ loading ? "Updating..." : "Update Post" }}
      </button>
    </form>

    <button @click="showDeleteModal = true" class="mt-4 w-full bg-red-500 text-white py-2 rounded hover:bg-red-600">
      Delete Post
    </button>

    <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div class="bg-white p-6 rounded-lg shadow-md">
        <p class="mb-4">Are you sure you want to delete this question? Type "delete" to confirm.</p>
        <input v-model="deleteConfirmation" type="text" class="w-full p-2 border rounded mb-4" />
        <div class="flex justify-end">
          <button @click="showDeleteModal = false" class="mr-2 bg-gray-300 py-2 px-4 rounded">Cancel</button>
          <button @click="deleteQuestion" class="bg-red-500 text-white py-2 px-4 rounded" :disabled="deleteLoading">
            {{ deleteLoading ? "Deleting..." : "Delete" }}
          </button>
        </div>
      </div>
    </div>
  </section>
</template>
