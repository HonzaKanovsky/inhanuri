<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();
const form = ref({
    username: "",
    password: ""
});
const errorMessage = ref("");

const loginUser = async () => {
    try {
        const response = await axios.post("/admin/login", form.value, {
            headers: { "Content-Type": "application/json" },
            withCredentials: true
        });

        alert("Login successful!");

        // Store userId and accessToken for future API requests
        localStorage.setItem("username", response.data.username);
        localStorage.setItem("accessToken", response.data.token);

        console.log(localStorage.getItem("username"))

        router.push("/"); // Redirect to dashboard
    } catch (error) {
        errorMessage.value = error.response?.data?.error || "Invalid login credentials.";
    }
};
</script>


<template>
    <div class="bg-slate-50 h-[80vh] flex flex-row">
        <div class="max-w-md mx-auto bg-white p-6 rounded-lg shadow-md self-center">
            <h2 class="text-2xl font-semibold text-center mb-4">Login</h2>

            <p v-if="errorMessage" class="text-red-500 text-center">{{ errorMessage }}</p>

            <form @submit.prevent="loginUser" class="space-y-4">
                <input v-model="form.username" type="text" placeholder="Username"
                    class="w-full p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-forest_green-600" required />
                <input v-model="form.password" type="password" placeholder="Password"
                    class="w-full p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-forest_green-600" required />

                <button type="submit"
                    class="w-full bg-forest_green-600 text-white p-2 rounded-md hover:bg-forest_green-400 transition">Login</button>
            </form>
        </div>
    </div>

</template>
