<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { QuillEditor } from "@vueup/vue-quill";
import axios from "axios";
import { tags } from "@/enums/tags";
import { CommunityEditRequest } from "@/models/CommunityEditRequest";

const router = useRouter();
const route = useRoute();

const community = ref(null);
const title = ref("");
const content = ref("");
const tag = ref("");
const password = ref("");
const loading = ref(false);
const token = ref(localStorage.getItem("editToken"));
const accessToken = ref(localStorage.getItem("accessToken"));
const isLoggedIn = ref(false);
const showDeleteModal = ref(false);
const deleteConfirmation = ref("");
const deleteLoading = ref(false);

const fetchCommunityPost = async () => {
  isLoggedIn.value = !!localStorage.getItem("accessToken");

  if (route.state?.community) {
    community.value = new CommunityEditRequest(route.state.community);
  } else {
    try {
      const response = await axios.get(`/community/${route.params.id}`);
      community.value = new CommunityEditRequest(response.data);
    } catch (error) {
      console.error("Error fetching community post:", error);
      alert("Failed to load post.");
      router.push("/community");
    }
  }

  if (community.value) {
    title.value = community.value.title;
    content.value = community.value.content;
    tag.value = community.value.tag;
  }
};

const updatePost = async () => {
  if (!title.value || !content.value || !tag.value) {
    alert("All fields are required.");
    return;
  }

  loading.value = true;
  const updateUrl = isLoggedIn.value? "/admin/community" : `/community/${route.params.id}`;
  const headers = isLoggedIn.value? { Authorization: `Bearer ${accessToken.value}` } : {};

  try {
    await axios.put(updateUrl, {
      id: community.value.id,
      title: title.value,
      content: content.value,
      tag: tag.value,
      password: isLoggedIn.value? undefined : password.value,
      token: isLoggedIn.value? undefined : token.value,
    }, { headers });

    alert("Post updated successfully!");
    router.push(`/community/${community.value.id}`);
  } catch (error) {
    console.error("Error updating community post:", error);
    alert(error);
  } finally {
    loading.value = false;
  }
};

const deletePost = async () => {
  if (deleteConfirmation.value !== "delete") {
    alert("Please type 'delete' to confirm deletion.");
    return;
  }

  deleteLoading.value = true;
  const deleteUrl = isLoggedIn.value? "/admin/community" : `/community/${route.params.id}`;
  const headers = isLoggedIn.value? { Authorization: `Bearer ${accessToken.value}` } : {};

  try {
    await axios.delete(deleteUrl, {
      headers,
      data: {
        id: community.value.id,
        token: isLoggedIn.value? undefined : token.value,
      },
    });

    alert("Post deleted successfully!");
    router.push("/community");
  } catch (error) {
    console.error("Error deleting community post:", error);
    alert("Failed to delete post.");
  } finally {
    deleteLoading.value = false;
    showDeleteModal.value = false;
  }
};

onMounted(fetchCommunityPost);
</script>


<template>
  <section class="max-w-4xl mx-auto p-6 bg-white shadow-md rounded-lg">
    <h1 class="text-3xl font-bold mb-6 text-center">Edit Community Post</h1>

    <form @submit.prevent="updatePost">
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
          class="w-full h-60 border rounded bg-white" />
      </div>

      <button type="submit" class="w-full bg-forest_green-600 text-white py-2 rounded hover:bg-forest_green-500 disabled:opacity-50"
        :disabled="loading">
        {{ loading ? "Updating..." : "Update Post" }}
      </button>
    </form>

    <button @click="showDeleteModal = true" class="mt-4 w-full bg-red-500 text-white py-2 rounded hover:bg-red-600">
      Delete Post
    </button>

    <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div class="bg-white p-6 rounded-lg shadow-md">
        <p class="mb-4">Are you sure you want to delete this post? Type "delete" to confirm.</p>
        <input v-model="deleteConfirmation" type="text" class="w-full p-2 border rounded mb-4" />
        <div class="flex justify-end">
          <button @click="showDeleteModal = false" class="mr-2 bg-gray-300 py-2 px-4 rounded">Cancel</button>
          <button @click="deletePost" class="bg-red-500 text-white py-2 px-4 rounded" :disabled="deleteLoading">
            {{ deleteLoading ? "Deleting..." : "Delete" }}
          </button>
        </div>
      </div>
    </div>
  </section>
</template>