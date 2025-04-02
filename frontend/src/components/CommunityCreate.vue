<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { QuillEditor } from "@vueup/vue-quill"; // ✅ Import Quill Editor
import axios from "axios";
import { tags } from "@/enums/tags";

const router = useRouter();

// Form Data
const title = ref("");
const content = ref(""); // Rich text content
const tag = ref(""); // Selected tag
const password = ref("");
const loading = ref(false);

// Submit the form
const submitForm = async () => {
  if (!title.value || !content.value || !tag.value || !password.value) {
    alert("All fields are required.");
    return;
  }

  if (!/^[0-9]{4}$/.test(password.value)) {
    alert("PIN must be a 4-digit number.");
    return;
  }

  loading.value = true;

  try {
    await axios.post(`/community`, {
      title: title.value,
      content: content.value,
      tag: tag.value,
      password: password.value,
    });

    alert("Community post created successfully!");
    router.push("/community"); // Redirect after success
  } catch (error) {
    console.error("Error creating community post:", error);
    alert(error);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <section class="max-w-4xl mx-auto p-6 bg-white shadow-md rounded-lg">
    <h1 class="text-3xl font-bold mb-6 text-center">Create Community Post</h1>

    <form @submit.prevent="submitForm">
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
          class="w-full h-60 border rounded bg-white" />
      </div>

      <!-- PIN -->
      <div class="mb-4">
        <label class="block text-gray-700">PIN:</label>
        <input v-model="password" type="password" class="w-full p-2 border rounded" required pattern="\d{4}"
          title="PIN must be a 4-digit number" />
        <p class="text-gray-500 text-sm mt-1">* This PIN can be used later to edit the post. It must be a 4-digit
          number.</p>
      </div>

      <!-- Submit Button -->
      <button type="submit"
        class="w-full bg-forest_green-600 text-white py-2 rounded hover:bg-forest_green-400 disabled:opacity-50"
        :disabled="loading">
        {{ loading ? "Submitting..." : "Create Post" }}
      </button>
    </form>
  </section>
</template>