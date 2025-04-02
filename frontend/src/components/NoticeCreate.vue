<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { QuillEditor } from "@vueup/vue-quill";
import Quill from 'quill';
import ImageUploader from 'quill-image-uploader';
import ImageResize from "quill-image-resize";
import axios from "axios";
import { tags } from "@/enums/tags";

const router = useRouter();
const title = ref("");
const content = ref("");
const tag = ref("");
const loading = ref(false);
const accessToken = ref(localStorage.getItem("accessToken"));
const uploadedFiles = ref([]);  // Store uploaded files

const submitForm = async () => {
  if (!title.value || !content.value || !tag.value) {
    alert("All fields are required.");
    return;
  }

  loading.value = true;

  try {
    await axios.post(
      "/notice",
      {
        title: title.value,
        content: content.value,
        tag: tag.value,
        fileIds: uploadedFiles.value.map(file => file.fileId),
      },
      {
        headers: {
          Authorization: `Bearer ${accessToken.value}`,
          "Content-Type": "application/json",
        },
      }
    );

    alert("Community post created successfully!");
    router.push("/notice");
  } catch (error) {
    console.error("Error creating notice post:", error);
    alert(error.response?.data?.message || "Failed to create post. Please try again.");
  } finally {
    loading.value = false;
  }
};

Quill.register('modules/imageUploader', ImageUploader);
Quill.register('modules/imageResize', ImageResize);

const quillOptions = {
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
  },
};

// Uploads a single file
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
</script>

<template>
  <section class="max-w-4xl mx-auto p-6 bg-white shadow-md rounded-lg">
    <h1 class="text-3xl font-bold mb-6 text-center">NEW NOTICE</h1>

    <form @submit.prevent="submitForm">
      <div class="mb-4">
        <label class="block text-gray-700">Title:</label>
        <input v-model="title" type="text" class="w-full p-2 border rounded" required />
      </div>

      <div class="mb-4">
        <label class="block text-gray-700">Tag:</label>
        <select v-model="tag" class="w-full p-2 border rounded" required>
          <option disabled value="">Select a tag</option>
          <option v-for="tagOption in tags" :key="tagOption" :value="tagOption">
            {{ tagOption }}
          </option>
        </select>
      </div>

      <div class="mb-4">
        <label class="block text-gray-700">Content:</label>
        <QuillEditor v-model:content="content" theme="snow" content-type="html"
          class="w-full border rounded bg-white h-[40vh]" :options="quillOptions" />
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

      <button type="submit"
        class="w-full bg-forest_green-600 text-white py-2 rounded hover:bg-forest_green-500 disabled:opacity-50"
        :disabled="loading">
        {{ loading ? "Submitting..." : "Create Post" }}
      </button>
    </form>
  </section>
</template>
