<script setup>
import { ref, onMounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";


import axios from "axios";
import { CommunityEditRequest } from "@/models/CommunityEditRequest";
import LikeButton from "./LikeButton.vue";

const route = useRoute();
const router = useRouter();
const questions = ref(null);
const showPasswordModal = ref(false);
const errorMessage = ref("");
const answers = ref([]);
const newAnswer = ref("");
const expandedAnswers = ref({});
const pinInputs = ref([]);
const pin = ref(["", "", "", ""]);

const isLoggedIn = ref(false);

const { t } = useI18n();

// Fetch question details
const fetchquestionsDetail = async () => {
  isLoggedIn.value = !!localStorage.getItem("accessToken");


  try {
    const response = await axios.get(`/questions/${route.params.id}`, {
      params: { isFirstVisit: checkFirstVisit() },
    });
    questions.value = response.data;
    answers.value = response.data.answers || []; // Load answers
  } catch (error) {
    console.error("Error fetching questions:", error);
  }
};
const toggleShowMore = (id) => {
  expandedAnswers.value[id] = !expandedAnswers.value[id];
};

const checkFirstVisit = () => {
  const path = route.path;

  const visitedPaths = JSON.parse(localStorage.getItem("visitedPaths")) || [];

  const isFirstVisit = !visitedPaths.includes(path);

  if (isFirstVisit) {
    visitedPaths.push(path);
    localStorage.setItem("visitedPaths", JSON.stringify(visitedPaths));
  }
  return isFirstVisit;
}

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
    const response = await axios.post(`/questions/validate-password`, {
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
        path: `/questions/edit/${route.params.id}`,
        state: { questions: editRequest }, // Pass via state
      });
    } else {
      errorMessage.value = "Incorrect PIN!";
    }
  } catch (error) {
    console.error("PIN validation failed:", error);
    errorMessage.value = "Failed to validate PIN.";
  }
};

const deleteQuestion = async (commentId) => {
  try {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
      console.error("Access token not found.");
      return;
    }

    const response = await axios.delete(`/questions/answer/${route.params.id}/${commentId}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (response.status === 200) {
      answers.value = response.data; // Update community with the new data
      answers.value = response.data.answers; //update comment array
    } else {
      console.error("Failed to delete comment:", response.status, response.data);
    }

  } catch (error) {
    console.error("Error deleting comment:", error);
  }
};
const cancelAnswer = () => {
  newAnswer.value = ""; // Clear input field
};
const postAnswer = async () => {
  if (!newAnswer.value.trim()) return;
  try {
    const response = await axios.post(`/questions/answer/${route.params.id}`, {
      content: newAnswer.value,
      isAdmin: isLoggedIn.value
    });
    answers.value = response.data.answers;
    newAnswer.value = ""; // Clear input
  } catch (error) {
    console.error("Error posting answer:", error);
  }
};

const handleEditClick = () => {
  console.log("isLoggedIn: " + isLoggedIn.value)
  if (isLoggedIn.value) {
    // No password needed, navigate directly
    const editRequest = new CommunityEditRequest({
      id: questions.value.id,
      title: questions.value.title,
      tag: questions.value.tag,
      updatedAt: questions.value.updatedAt,
      views: questions.value.views,
      token: localStorage.getItem("accessToken"), // Use access token
    });

    router.push({
      path: `/questions/edit/${route.params.id}`,
      state: { questions: editRequest },
    });
  } else {
    // Show password modal for non-logged-in users
    showPasswordModal.value = true;
  }
};


// Format date
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString();
};

// ✅ Back button function
const goBack = () => {
  router.push("/questions"); // Navigates back to the questions list
};

onMounted(fetchquestionsDetail);
</script>

<template>
  <section class="bg-slate-50">
    <div class="max-w-5xl mx-auto flex flex-col pt-20 min-h-[80vh]">
      <h1 class="text-5xl font-bold gradient-background-right bg-clip-text text-transparent mx-auto">
        {{ t("questions.title") }}
      </h1>

      <div v-if="questions">
        <div class="flex flex-row items-center mt-5">
          <span class="flex my-4 text-gray-500 text-sm">
            <button @click="goBack" class=" px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-800">
              ← Back to questions
            </button>
          </span>
          <span class="flex my-4 text-gray-500 text-sm ml-auto">
            <button @click="handleEditClick" class="px-4 py-2 bg-sgbus_green-200 text-white rounded hover:bg-gray-800">
              Edit
            </button>
          </span>
        </div>
        <span class="flex flex-row">
          <LikeButton class="flex mr-auto" :targetId="questions.id" targetType="QUESTION"
            :InitialLikes="questions.likes" />
          <div class="flex mb-0 mt-auto mr-0">
            <span class="flex text-gray-500 text-base font-semibold ml-auto">Views:&nbsp; </span>
            <span class="flex text-gray-500 text-base">{{ questions.views }} |&nbsp;</span>
            <span class="flex text-gray-500 text-base font-semibold">Created:&nbsp; </span>
            <span class="flex text-gray-500 text-base"> {{ formatDate(questions.createdAt) }} |&nbsp;</span>
            <span class="flex text-gray-500 text-base font-semibold">Updated:&nbsp; </span>
            <span class="flex text-gray-500 text-base"> {{ formatDate(questions.updatedAt) }}</span>
          </div>
        </span>
        <div class="bg-black_olive-700 shadow-lg rounded-t-lg p-6">
          <h1 class="text-3xl font-bold text-center text-white">{{ questions.title }}</h1>
        </div>
        <div class="bg-white shadow-lg rounded-b-lg p-6">
          <div v-html="questions.content" class="prose max-w-none"></div>
        </div>
      </div>

      <div v-else class="text-center text-gray-500">Loading...</div>



      <!-- ✅ Answer Submission Form -->
      <div class="mt-6">
        <h2 class="text-2xl font-semibold mb-4">Answers</h2>
        <textarea v-model="newAnswer" class="w-full p-2 border rounded" placeholder="Add a comment..."></textarea>
        <div class="flex space-x-2 mt-2">
          <button v-if="newAnswer.trim()" @click="postAnswer"
            class="px-4 py-2 bg-forest_green-500 text-white rounded hover:bg-forest_green-600">
            Submit Answer
          </button>
          <button v-if="newAnswer.trim()" @click="cancelAnswer"
            class="px-4 py-2 bg-gray-400 text-white rounded hover:bg-gray-500">
            Cancel
          </button>
        </div>
      </div>


      <div v-if="answers.length" class="">
        <div v-for="answer in answers" :key="answer.id" class="flex flex-col ml-4">
          <div class="flex flex-col">
            <div class="flex flex-row items-center">
              <h1 v-if="answer.isAdmin" class="font-bold text-spring_green-300">admininistrator</h1>
              <h1 v-else class="font-bold">anonymous user</h1>
              <span class="pl-2 text-cal_poly_green-500 text-sm">Created: {{ formatDate(answer.createdAt) }}</span>
              <button v-if="isLoggedIn" @click="deleteQuestion(answer.id)" class="ml-auto text-red-500">Delete</button>
            </div>
            <p>
              {{ expandedAnswers[answer.id] || answer.content.length <= 200 ? answer.content : answer.content.slice(0,
                200) + '...' }} <button v-if="answer.content.length > 200" @click="toggleShowMore(answer.id)"
                class="text-forest_green-600 ml-2">
                {{ expandedAnswers[answer.id] ? "Show Less" : "Show More" }}
                </button>
            </p>
          </div>
          <div class="flex">
            <LikeButton class="flex mr-auto" :targetId="answer.id" targetType="ANSWER" :InitialLikes="answer.likes" />
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
