<script setup>
import { ref, onMounted,nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

import axios from "axios";
import { CommunityEditRequest } from "@/models/CommunityEditRequest";
import LikeButton from "@/components/LikeButton.vue";

const route = useRoute();
const router = useRouter();
const community = ref(null);
const showPasswordModal = ref(false);
const password = ref("");
const errorMessage = ref("");
const comments = ref([]);
const newComment = ref("");
const expandedComments = ref({});
const pinInputs = ref([]);
const pin = ref(["", "", "", ""]);


const isLoggedIn = ref(false);

const { t } = useI18n();

// Fetch community details
const fetchcommunityDetail = async () => {
  isLoggedIn.value = !!localStorage.getItem("accessToken");

  try {
    const response = await axios.get(`/community/${route.params.id}`, {
      params: { isFirstVisit: checkFirstVisit() },
    });;
    community.value = response.data;
    comments.value = response.data.comments || []; // Load answers
  } catch (error) {
    console.error("Error fetching community:", error);
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
// Format date
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString();
};

// ✅ Back button function
const goBack = () => {
  router.push("/community"); // Navigates back to the communitys list
};

const handleInput = async (event, index) => {
  const value = event.target.value.replace(/\D/g, ""); // Allow only digits
  pin.value[index] = value;

  if (value && index < 3) {
    await nextTick();
    pinInputs.value[index + 1]?.focus();
  }
};

const handleDelete = async (index) => {
  if (!pin.value[index] && index > 0) {
    await nextTick();
    pinInputs.value[index - 1]?.focus();
  }
};

// Validate the PIN entered by the user
const validatePin = async () => {
  if (pin.value.length !== 4) {
    errorMessage.value = "Please enter a 4-digit PIN.";
    return;
  }

  try {
    const response = await axios.post(`/community/validate-password`, {
      id: Number(route.params.id),
      password: Number(pin.value.join("")), // Use the PIN entered by the user
    });

    if (response.status === 200) {
      const token = response.data.token;
      localStorage.setItem("editToken", token);

      const editRequest = {
        id: route.params.id,
        token: token, // Include token for the edit request
      };

      showPasswordModal.value = false;
      router.push({
        path: `/community/edit/${route.params.id}`,
        state: { community: editRequest }, // Pass via state
      });
    } else {
      errorMessage.value = "Incorrect PIN!";
    }
  } catch (error) {
    console.error("PIN validation failed:", error);
    errorMessage.value = "Failed to validate PIN.";
  }
};

const toggleShowMore = (id) => {
  expandedComments.value[id] = !expandedComments.value[id];
};

const cancelAnswer = () => {
  newComment.value = ""; // Clear input field
};

const handleEditClick = () => {
  console.log("isLoggedIn: " + isLoggedIn.value);
  if (isLoggedIn.value) {
    // No password needed, navigate directly
    const editRequest = new CommunityEditRequest({
      id: community.value.id,
      title: community.value.title,
      tag: community.value.tag,
      updatedAt: community.value.updatedAt,
      views: community.value.views,
      token: localStorage.getItem("accessToken"), // Use access token
    });

    router.push({
      path: `/community/edit/${route.params.id}`,
      state: { community: editRequest },
    });
  } else {
    // Show password modal for non-logged-in users
    showPasswordModal.value = true;
  }
};

const postComment = async () => {
  if (!newComment.value.trim()) return;
  try {
    const response = await axios.post(`/community/comment/${route.params.id}`, {
      content: newComment.value,
      isAdmin: isLoggedIn.value
    });
    comments.value = response.data.comments;
    newComment.value = ""; // Clear input
  } catch (error) {
    console.error("Error posting comment:", error);
  }
};

const deleteComment = async (commentId) => {
  try {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
      console.error("Access token not found.");
      return;
    }

    const response = await axios.delete(`/community/comment/${route.params.id}/${commentId}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (response.status === 200) {
      community.value = response.data; // Update community with the new data
      comments.value = response.data.comments; //update comment array
    } else {
      console.error("Failed to delete comment:", response.status, response.data);
    }

  } catch (error) {
    console.error("Error deleting comment:", error);
  }
};

// Fetch data on mount
onMounted(fetchcommunityDetail);
</script>

<template>
  <section class="bg-slate-50">
    <div class="max-w-5xl mx-auto flex flex-col pt-20 min-h-[80vh]" :key="community ? community.updatedAt : 0">
      <h1 class="text-5xl font-bold gradient-background-right bg-clip-text text-transparent mx-auto">
        {{ t("community.title") }}
      </h1>

      <div v-if="community">
        <div class="flex flex-row items-center mt-5">
          <span class="flex my-4 text-gray-500 text-sm">
            <button @click="goBack" class="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-800">
              ← Back to communities
            </button>
          </span>
          <span class="flex my-4 text-gray-500 text-sm ml-auto">
            <button @click="handleEditClick" class="px-4 py-2 bg-sgbus_green-200 text-white rounded hover:bg-gray-800">
              Edit
            </button>
          </span>
        </div>
        <span class="flex flex-row">
          <LikeButton class="flex mr-auto" :targetId="community.id" targetType="NOTICE" :InitialLikes="community.likes" />
          <div class="flex mb-0 mt-auto mr-0">
            <span class="flex text-gray-500 text-base font-semibold ml-auto">Views:&nbsp; </span>
            <span class="flex text-gray-500 text-base">{{ community.views }} |&nbsp;</span>
            <span class="flex text-gray-500 text-base font-semibold">Created:&nbsp; </span>
            <span class="flex text-gray-500 text-base"> {{ formatDate(community.createdAt) }} |&nbsp;</span>
            <span class="flex text-gray-500 text-base font-semibold">Updated:&nbsp; </span>
            <span class="flex text-gray-500 text-base"> {{ formatDate(community.updatedAt) }}</span>
          </div>
        </span>
        <div class="bg-black_olive-700 shadow-lg rounded-t-lg p-6">
          <h1 class="text-3xl font-bold text-center text-white">{{ community.title }}</h1>
        </div>
        <div class="bg-white shadow-lg rounded-b-lg p-6">
          <div v-html="community.content" class="prose max-w-none"></div>
        </div>
      </div>

      <div v-else class="text-center text-gray-500">Loading...</div>

      <div class="mt-6">
        <h2 class="text-2xl font-semibold mb-4">Comments</h2>
        <textarea v-model="newComment" class="w-full p-2 border rounded" placeholder="Add a comment..."></textarea>
        <div class="flex space-x-2 mt-2">
          <button v-if="newComment.trim()" @click="postComment" class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-forest_green-600">
            Submit Comment
          </button>
          <button v-if="newComment.trim()" @click="cancelAnswer" class="px-4 py-2 bg-gray-400 text-white rounded hover:bg-gray-500">
            Cancel
          </button>
        </div>
      </div>

      <div v-if="comments.length" class="">
        <div v-for="comment in comments" :key="comment.id" class="flex flex-col ml-4">
          <div class="flex flex-col">
            <div class="flex flex-row items-center">
              <h1 v-if="comment.isAdmin" class="font-bold text-spring_green-300">admininistrator</h1>
              <h1 v-else class="font-bold">anonymous user</h1>
              <span class="pl-2 text-cal_poly_green-500 text-sm">Created: {{ formatDate(comment.createdAt) }}</span>
              <button v-if="isLoggedIn" @click="deleteComment(comment.id)" class="ml-auto text-red-500">Delete</button>
            </div>
            <p>
              {{ expandedComments[comment.id] || comment.content.length <= 200 ? comment.content : comment.content.slice(0,
                200) + '...' }} <button v-if="comment.content.length > 200" @click="toggleShowMore(comment.id)"
                class="text-forest_green-600 ml-2">
                {{ expandedComments[comment.id] ? "Show Less" : "Show More" }}
                </button>
            </p>
          </div>
          <div class="flex">
            <LikeButton class="flex mr-auto" :targetId="comment.id" targetType="COMMENT" :InitialLikes="comment.likes" />
          </div>
        </div>
      </div>
    </div>
    <!-- Pop-up Modal for PIN input -->
    <div v-if="showPasswordModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <div class="bg-white p-6 rounded-lg shadow-lg w-96">
        <h2 class="text-lg font-bold mb-4 text-center">Enter 4-Digit PIN</h2>

        <div class="flex justify-center space-x-2">
          <input v-for="(digit, index) in pin" :key="index" v-model="pin[index]" type="text" maxlength="1"
            pattern="[0-9]" inputmode="numeric"
            class="w-12 h-12 text-center text-lg border rounded focus:ring-2 focus:ring-forest_green-500"
            @input="handleInput($event, index)" @keyup.delete="handleDelete(index)" ref="pinInputs" />
        </div>

        <div class="flex justify-end mt-4 space-x-2">
          <button @click="showPasswordModal = false" class="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400">
            Cancel
          </button>
          <button @click="validatePin"
            class="px-4 py-2 bg-forest_green-500 text-white rounded hover:bg-forest_green-600">
            Submit
          </button>
        </div>

      </div>
    </div>
  </section>
</template>