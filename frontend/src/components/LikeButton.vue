<script setup>
import { ref, defineProps, computed, onMounted } from "vue";
import axios from "axios";
import { PhArrowFatDown, PhArrowFatUp } from "@phosphor-icons/vue";

const props = defineProps({
  targetId: Number,
  targetType: String, // NOTICE, COMMENT, QUESTION, ANSWER
  InitialLikes: Number,
  InitialVote: String // LIKE, DISLIKE, or NEUTRAL
});

const likes = ref(0);
const userVote = ref("NEUTRAL");

// Determine if the layout should be column-based
const isColumnLayout = false//computed(() => ["COMMENT", "ANSWER"].includes(props.targetType));

// Initialize likes and user vote state
onMounted(() => {
  likes.value = props.InitialLikes;
  userVote.value = props.InitialVote;
});

const sendLikeRequest = async (likeType) => {
  try {
    // Toggle behavior: If the same button is clicked again, reset to NEUTRAL
    const newVote = userVote.value === likeType ? "NEUTRAL" : likeType;

    const response = await axios.post(`/like`, {
      id: props.targetId,
      targetType: props.targetType,
      likeType: newVote,
    });

    likes.value = response.data.likes;
    userVote.value = newVote; // Update state
  } catch (error) {
    console.error("Error updating like status:", error);
    userVote.value = likeType;
  }
};
</script>

<template>
  <div class="flex items-center space-x-2 border rounded-3xl bg-white p-1 mb-2"
    :class="isColumnLayout ? 'flex-col space-x-0 space-y-1' : 'flex-row space-y-0 space-x-2'">
    <!-- Like Button -->
    <button @click="sendLikeRequest('LIKE')" :class="{ 'text-green-500': userVote === 'LIKE' }"
      class=" rounded hover:bg-gray-200">
      <PhArrowFatUp :size="24" />
    </button>

    <span class="text-gray-600 font-medium"> {{ likes }} </span>

    <!-- Dislike Button -->
    <button @click="sendLikeRequest('DISLIKE')" :class="{ 'text-red-500': userVote === 'DISLIKE' }"
      class="rounded hover:bg-gray-200">
      <PhArrowFatDown :size="24" />
    </button>
  </div>
</template>
