import axios from "axios";
import { AUTH_SERVICE_URL } from "../utils/constants";

const authApi = axios.create({
  baseURL: AUTH_SERVICE_URL,
  headers: { "Content-Type": "application/json" }
});

export async function login(credentials) {
  const response = await authApi.post("/auth/login", credentials);
  return response.data;
}

export async function signup(payload) {
  const response = await authApi.post("/auth/register", payload);
  return response.data;
}
